import pandas as pd
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
import catahelper
import pygsheets
import csv

# authorization
gc = pygsheets.authorize(service_file="/Users/jiyoojeong/Desktop/americancultures/client_secret.json")

# set up webdriver
options = webdriver.ChromeOptions()
options.add_argument('--ignore-certificate-errors')
options.add_argument('--incognito')
options.add_argument('--headless')
driver = webdriver.Chrome("/Users/jiyoojeong/Downloads/chromedriver", options=options)

main_url = "https://classes.berkeley.edu/search/class/?f%5B0%5D=im_field_term_name%" \
           "3A865&f%5B1%5D=sm_general_requirement%3AAmerican%20Cultures"

driver.get(main_url)
urls = {'1': main_url}
page_number = 2
while True:
    try:
        link = driver.find_element_by_link_text(str(page_number))
    except NoSuchElementException:
        break
    link.click()
    urls[str(page_number)] = str(driver.current_url)
    page_number += 1

# print(urls)  # prints all urls from search results

courses_split = []
instructor_list = []
depts = []

soup = BeautifulSoup(driver.page_source, "lxml")
yr = soup.find("div", "ls-term-year")
yr = yr.get_text()

yrs = []

# open the sheet
file = gc.open('AC Classes List')
sheet = file[0]  # selects the first sheet


# create a new sheet for every year
def write_sheet(year):
    # col 1 dept, col 2 course num, col 3-n all instructors

    data_dict = {"Department": [i[0] for i in courses_split], "Course Number": [i[1] for i in courses_split],
                 "Instructors": instructor_list, "Department Full": depts}
    print(len(data_dict["Department"]))
    print(len(data_dict["Course Number"]))
    print(len(data_dict["Instructors"]))
    print(len(data_dict["Department Full"]))
    # print("dept:" + " len = " + str(len(data_dict["Department"])) + " " + str(data_dict["Department"]))
    # print("numb:" + " len = " + str(len(data_dict["Course Number"])) + " " + str(data_dict["Course Number"]))
    # print("inst:" + " len = " + str(len(data_dict["Instructors"])) + " " + str(data_dict["Instructors"]))
    df = pd.DataFrame(data_dict)
    df2 = df.Instructors.apply(pd.Series)  # separates instructors
    lis = []
    for c in df2.columns:
        lis.append("Instructor " + str(c + 1))
    df2.columns = lis
    df2.insert(0, "Course Number", data_dict['Course Number'])
    df2.insert(0, "Department", data_dict['Department'])
    df2.insert(1, "Department_FULL", data_dict['Department Full'])

    print(df2)  # check
    return df2


# use cata-helper to go through each page
for page in urls:
    print("running page " + page)

    helper_course, helper_instructors, helper_yr, helper_depts = catahelper.main(urls[page])

    courses_split.extend(helper_course)
    instructor_list.extend(helper_instructors)
    depts.extend(helper_depts)

    if yr != helper_yr:
        # print("yr change")
        yrs.append(helper_yr)
        # write_new_sheet(helper_yr)
        yr = helper_yr
        courses_split = []
        instructor_list = []
    else:
        wow = 2
        # courses_split = []
        # instructor_list = []

# update sheet on google drive
sheet.title = yr
with open('data/current_data_sem_year.csv', 'w') as f:
    writer = csv.writer(f)
    writer.writerow([yr])
print("process completed.")
sheet.set_dataframe(write_sheet(yr), (1, 1))
