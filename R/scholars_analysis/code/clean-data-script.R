####################
# title: clean-data-script
# author: Jiyoo Jeong
# date: 12/6/2019
#
# Desc: Extraction and cleaning of Raw Data
# output: clean data in the form of csv file saved in the directory ../data/cleandata for warmup 03
####################

library(xml2)
library(rvest)
library(dplyr)
Sys.sleep(15)

# downloaded html files from github.
path = "./desktop/stat133/workouts-jiyoojeong/workout3/"

# Read the HTML files

#Abhijit Banerjee
ab <- read_html(paste0(path, 'data/rawdata/abhijit_banerjee_GoogleScholarCitations.html'))
## syntax 
t.ab <- html_table(ab)
head(t.ab)


#Esther Duflo
ed <- read_html(paste0(path, 'data/rawdata/esther_duflo_GoogleScholarCitations.html'))
## syntax
t.ed <- html_table(ed)


### Names
ab_name <- ab %>% 
  html_nodes(xpath= '//*[@id="gsc_prf_in"]') %>%
  html_text()
ab_name

ed_name <- ed %>% 
  html_nodes(xpath= '//*[@id="gsc_prf_in"]') %>%
  html_text()
ed_name

### Affilitated Institutions

ab_inst <- ab %>% 
  html_nodes(xpath= '//*[@class="gsc_prf_ila"]') %>%
  html_text()
if (length(ab_inst)==0) {
  ab_inst <- NA
}
ab_inst


ed_inst <- ed %>% 
  html_nodes(xpath= '//*[@class="gsc_prf_ila"]') %>%
  html_text()
if (length(ed_inst)==0) {
  ed_inst <- NA
}
ed_inst




# Extract all papers of each author

#' @name: all_papers
#' @description: collects all the paper information for a given author
#' @params: parent_data = dataframe of the read html file for a given author
#' @output: a data frame of all the file information
all_papers <- function(parent_data) {
  paperName <- parent_data %>% 
    html_nodes(xpath = '//*[@class="gsc_a_at"]') %>%
    html_text()
  
  rj <- parent_data %>%
    html_nodes(xpath='//*[@class="gs_gray"]') %>%
    html_text()
  # print(rj[1])
  # print(matrix(rj,nrow=2, ncol=492/2 ))
  if (grepl("sargam_jain", rj[1], fixed=TRUE)) {
    rj <- rj[4:length(rj)]
    print(rj)
  }
  researcher <- rj[seq(1, length(rj)-1, by=2)]
  
  journal <- rj[seq(2,length(rj), by=2)]
  
  
  citations <- parent_data %>%
    html_nodes(xpath = '//*[@class="gsc_a_c"]') %>%
    html_nodes("a") %>%
    html_text()
  
  citations <- parent_data %>%
    html_nodes(xpath = '//*[@class="gsc_a_ac gs_ibl"]') %>%
    html_text()
  
  # print(citations)
  # citations <- citations[-which(citations=="*")]
  # print(citations)
  
  year <- parent_data %>%
    html_nodes(xpath='//*[@class="gsc_a_h gsc_a_hc gs_ibl"]') %>%
    html_text()
  
  print(length(paperName))
  print(length(researcher))
  print(length(journal))
  print(length(citations))
  print(length(year))
  
  df <- data.frame(paperName, researcher, journal, citations, year)
  return(df)
}



# Abjijit
df.ab <- all_papers(ab)
write.csv(df.ab, file=paste0(path, "data/cleandata/abhijit_banerjee_gsc.csv"))

# Esther
df.ed <- all_papers(ed)
write.csv(df.ed, paste0(path, "data/cleandata/esther_duflo_gsc.csv"))




