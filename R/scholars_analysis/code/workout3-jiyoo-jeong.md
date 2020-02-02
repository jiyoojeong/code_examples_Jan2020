workout3-jiyoo-jeong
================
Jiyoo Jeong
12/6/2019

## Comparing Abhijit Banerjee and Esther Duflo

### read clean data.

``` r
ab <- read.csv("../data/cleandata/abhijit_banerjee_gsc.csv")
ed <- read.csv("../data/cleandata/esther_duflo_gsc.csv")

df.ab <- as.data.frame(ab, header=TRUE, stringsAsFactors= FALSE)
df.ed <- as.data.frame(ed, header=TRUE, stringsAsFactors= FALSE)
```

### Some Regex Analysis

First we will begin with simple regex analysis of the data.

### Starting of paperName is a Vowel

#### A. Banerjee

``` r
#filter through the data frame using dplyr
filtered_papers <- df.ab %>% 
  filter(grepl('^[AEIOU]', paperName)) %>%  
  select(paperName)
# find the length of the paperName column
length(filtered_papers[[1]])
```

    ## [1] 118

#### E. Duflo

``` r
filtered_papers <- df.ed %>% 
  filter(grepl('^[AEIOU]', paperName)) %>%
  select(paperName)

length(filtered_papers[[1]])
```

    ## [1] 116

### Ends with “s”

#### A. Banerjee

``` r
library(stringr)
#filter through the data frame using dplyr
filtered_papers <- df.ab %>% 
  filter(grepl('s$', paperName)) %>%  
  select(paperName)
# find the length of the new paperName column
length(filtered_papers[[1]])
```

    ## [1] 78

#### E. Duflo

``` r
library(stringr)
#filter through the data frame using dplyr
filtered_papers <- df.ed %>% 
  filter(grepl('s$', paperName)) %>%  
  select(paperName)
# find the length of the paperName column
length(filtered_papers[[1]])
```

    ## [1] 74

### Longest title

#### A. Banerjee

``` r
paperName_ab <- lapply(df.ab$paperName, as.character) # due to class errors.
lens <- nchar(paperName_ab) #vector of lengths
longtitle_ab <- paperName_ab[which.max(lens)]
longtitle_ab[[1]]
```

    ## [1] "Voters be Primed to Choose Better Legislators? Experimental Evidence from Rural India,” October 2010. mimeo, Harvard Universiy. 4, 27, 29, Selvan Kumar, Rohini Pande, and Felix …"

#### E. Duflo

``` r
paperName_ed <- lapply(df.ed$paperName, as.character)
lens <- nchar(paperName_ed)
longtitle_ed <- paperName_ed[which.max(lens)]
longtitle_ed[[1]]
```

    ## [1] "Controlling the costs of HIV/AIDS management--unique management software to empower organisations to formulate and implement strategies best suited for their specific requirements."

### Number of punctuation symbols in titles

#### A. Banerjee

``` r
# make vector of paper titles
papers_ab <- lapply(df.ab$paperName, as.character)
# remove all alpha numeric values and spaces
punctuations_ab <- gsub("[[:alnum:]]| ", '', papers_ab)

#count lens in each row of strip
counts_ab <- df.ab %>% 
  mutate("counts" = nchar(punctuations_ab)) %>%
  select(paperName, counts)

head(counts_ab)
```

    ##                                                                 paperName
    ## 1                                         A simple model of herd behavior
    ## 2                      Occupational choice and the process of development
    ## 3 Poor economics: A radical rethinking of the way to fight global poverty
    ## 4      The miracle of microfinance? Evidence from a randomized evaluation
    ## 5                                          The economic lives of the poor
    ## 6                           Inequality and growth: What can the data say?
    ##   counts
    ## 1      0
    ## 2      0
    ## 3      1
    ## 4      1
    ## 5      0
    ## 6      2

#### E. Duflo

``` r
# make vector of paper titles
papers_ed <- lapply(df.ed$paperName, as.character)
# remove all alpha numeric values and spaces
punctuations_ed <- gsub("[[:alnum:]]| ", '', papers_ed)

#count lens in each row of strip
counts_ed <- df.ed %>% 
  mutate("counts" = nchar(punctuations_ed)) %>%
  select(paperName, counts)

head(counts_ed)
```

    ##                                                                                                                 paperName
    ## 1                                                          How much should we trust differences-in-differences estimates?
    ## 2                                                 Poor economics: A radical rethinking of the way to fight global poverty
    ## 3 Schooling and labor market consequences of school construction in Indonesia: Evidence from an unusual policy experiment
    ## 4                                                      The miracle of microfinance? Evidence from a randomized evaluation
    ## 5                         Grandmothers and granddaughters: old‐age pensions and intrahousehold allocation in South Africa
    ## 6                                                                                          The economic lives of the poor
    ##   counts
    ## 1      3
    ## 2      1
    ## 3      1
    ## 4      1
    ## 5      2
    ## 6      0

### Remove Stop Words, Numbers, Punctuations from titles

#### A. Banerjee

``` r
stop <- c("the", "a", "an", "and", "in", "if", "but")
#regex patterns to remove. 
to.remove <- "\\b(the|The|a|A|an|An|and|And|in|In|if|If|but|But)\\b"
pd <- "[[:punct:]]|[[:digit:]]"


df.removed.ab <- df.ab %>% 
  mutate("removedName"= gsub(to.remove, " ", paperName)) %>%
  mutate("removedName" = gsub("-", " ", removedName)) %>%
  mutate("removedName" = gsub(pd, "", removedName))
  
df.rem.ab <- select(df.removed.ab, removedName, paperName)
```

#### E. Duflo

``` r
df.removed.ed <- df.ed %>% 
  mutate("removedName"= gsub(to.remove, " ", paperName)) %>%
  mutate("removedName" = gsub("-", " ", removedName)) %>%
  mutate("removedName" = gsub(pd, "", removedName))
  
df.rem.ed <- select(df.removed.ed, removedName, paperName)
```

### A: A. Banerjee’s 10 most frequent words

``` r
# vector of removed paper names.
r.ab <- df.rem.ab$removedName

# make all words lowercase
r.ab <- tolower(r.ab)
# combine vector into one string
s <- paste(r.ab, collapse=" ")
```

``` r
# split string into a vector containing all words.
v <- strsplit(s, " ")
df.v <- data.frame(v, stringsAsFactors = FALSE)
colnames(df.v) <- c("word")

# remove empty strings and spaces
df.v <- filter(df.v, word != "" & word != " " & length(word) > 1)

# create a table to count freqs
words.freq <- table(df.v)

# bind table into a data frame
words.freq <- cbind.data.frame(names(words.freq),as.integer(words.freq), stringsAsFactors=FALSE)
colnames(words.freq) <-  c("word", "count")

# order the data frame with highest counts to lowest
w.count <- words.freq[with(words.freq, order(-count)),]

# display 10 most frequent words
head(w.count, 10)
```

    ##             word count
    ## 855           of   176
    ## 514         from    68
    ## 448     evidence    67
    ## 1214          to    67
    ## 606        india    46
    ## 334  development    42
    ## 500          for    41
    ## 384    economics    38
    ## 361           dp    30
    ## 382     economic    29

### B: Esther Duflo’s 10 most frequent words

``` r
# vector of removed paper names.
r.ed <- df.rem.ed$removedName

# make all words lowercase
r.ed <- tolower(r.ed)
# combine vector into one string
s.b <- paste(r.ed, collapse=" ")
```

``` r
# split string into a vector containing all words.
v.b <- strsplit(s.b, " ")
df.b <- data.frame(v.b, stringsAsFactors = FALSE)
colnames(df.b) <- c("word")

# remove empty strings and spaces
df.b <- filter(df.b, word != "" & word != " " & length(word) > 1)

# create a table to count freqs
w.b.freq <- table(df.b)

# bind table into a data frame
w.b.freq <- cbind.data.frame(names(w.b.freq), 
                               as.integer(w.b.freq), 
                               stringsAsFactors=FALSE)
colnames(w.b.freq) <-  c("word", "count")

# order the data frame with highest counts to lowest
w.b.count <- w.b.freq[with(w.b.freq, order(-count)),]

# display 10 most frequent words
head(w.b.count, 10)
```

    ##             word count
    ## 812           of   179
    ## 496         from   103
    ## 436     evidence    98
    ## 1182          to    79
    ## 479          for    57
    ## 595        india    56
    ## 975   randomized    52
    ## 371    economics    49
    ## 317  development    48
    ## 818           on    37

### Digging into word frequencies.

To visualize the frequencies calculated in the previous step, we will
now use the R library, word cloud.
<a href="https://cran.r-project.org/web/packages/wordcloud/wordcloud.pdf">Read
more about wordcloud</a>. The data visualization code can be found in
<a href="functions.R">functions.R</a>

#### Banerjee:

<img src="../images/wordcloud_banerjee.png" style="display: block; margin: auto;" />

#### Duflo:

<img src="../images/wordcloud_duflo.png" style="display: block; margin: auto;" />

We can observe here that there are quite a lot of overlap between the
two scholars. To further analyze this, I will analyze next the number of
publications throughout the years of the two
scholars.

<img src="../images/publications_banerjee.png" style="display: block; margin: auto;" /><img src="../images/publications_duflo.png" style="display: block; margin: auto;" />

While banerjee had an early start to his research career, both scholars
picked up on the frequency of their publications by year 2012, which is
the peak for both of them. While Banerjee remeains steady after, Duflo
does show a slightly downward trend.

Interestingly, of the top 10 most frequent words in the titles, both
Banerjee and Duflo shared multiple words. I will be analyzing the words
“india”, “economics”, “development” and “evidence”

Plotting the frequencies of these words by year we can see that with the
growth of publications they all rise. However, it differs between the
two scholars
slightly:

<img src="../images/wc_banerjee.png" style="display: block; margin: auto;" /><img src="../images/wc_duflo.png" style="display: block; margin: auto;" />

We can see that both Banerjee and Duflo spiked in the usage of these
words expeically between they year 2000 and 2010. However, both parties
in the last decade have steered away from using the four terms, with the
exception of Banerjee and his relationship with “evidence”, as it has
risen steadily since the year 2000.

### Exploring More Questions:

While scrolling through the original website, there were a distinct
number of journals and papers that seemed to reference one another as
their co-authors.

Digging into this deeper, I have analyzed the following relationships
regarding the co-authors of the journals of each scholar.

#### Q1: whats the paper with the most coauthors?

To approach this problem, I will follow a similar analysis as the
filtered papers as before. Only this time, I will be counting the “,”
symbol in regex.

``` r
#filter through the data frame using dplyr

#Banerjee
researchers <- df.ab %>% 
  mutate(
    "author_count" = ifelse(
      grepl("Unpublished paper", as.character(researcher), fixed=TRUE),
      1,
      str_count(gsub(', [[:digit:]]',"", as.character(researcher)), ',') + 1)) %>%
  select(paperName, researcher, author_count)

researchers[which.max(researchers$author_count),]
```

    ##                     paperName
    ## 301 Russia’s Phony Capitalism
    ##                                                                   researcher
    ## 301 R Das, R Das, R Das, R Das, R Das, R Das, B Bhattacharya, S Yechury, ...
    ##     author_count
    ## 301            9

``` r
ab_co <- mean(researchers$author_count)
```

``` r
#Duflo
researchers <- df.ed %>% 
  mutate(
    "author_count" = ifelse(
      grepl("Unpublished paper", as.character(researcher), fixed=TRUE),
      1,
      str_count(gsub(', [[:digit:]]',"", as.character(researcher)), ',') + 1)) %>%
  select(paperName, researcher, author_count)

# researchers[with(researchers, order(-author_count)),]
researchers[which.max(researchers$author_count),]
```

    ##                                                                                        paperName
    ## 31 A multifaceted program causes lasting progress for the very poor: Evidence from six countries
    ##                                                                       researcher
    ## 31 A Banerjee, E Duflo, N Goldberg, D Karlan, R Osei, W Parienté, J Shapiro, ...
    ##    author_count
    ## 31            8

``` r
ed_co <- mean(researchers$author_count)
```

Between both scholars, the paper, “Russia’s Phony Capitalism” has the
most co-authors.

#### Q2: on avg who had more co-authors?

``` r
# see above for calc
#banerjee
ab_co
```

    ## [1] 3.573737

``` r
# duflo
ed_co
```

    ## [1] 3.317719

On average Banerjee had higher co-author counts.

#### Q3: do the two scholars have mutual friends?

``` r
ab_co_authors <- df.ab %>% 
  mutate(
    "authors" = ifelse(
      grepl("Unpublished paper", as.character(researcher), fixed=TRUE),
      "",
      str_split(gsub(', [[:digit:]]',"", as.character(researcher)), ','))) %>%
  select(paperName, researcher, authors)

# remove scholars
ab.co <- gsub(',|A Banerjee|A. Banerjee|A. Banerjee.|A BANERJEE|E Duflo', "", ab_co_authors$authors)
# formatting to be prettier
ab.co <- gsub('\"|c|[()]|[...]', "", ab.co)
ab.co <- trimws(ab.co, which=c("both"))
ab.co <- paste(ab.co, collapse="&")
ab.co <- strsplit(ab.co, "&|  ")
ab.co <- ab.co[[1]]

# unique list of co-authors from Banerjee's papers
ab.co <- unique(toupper(ab.co))
ab.co <- ab.co[ab.co != ""]
```

``` r
ed_co_authors <- df.ed %>% 
  mutate(
    "authors" = ifelse(
      grepl("Unpublished paper", as.character(researcher), fixed=TRUE),
      "",
      str_split(gsub(', [[:digit:]]',"", as.character(researcher)), ','))) %>%
  select(paperName, researcher, authors)

# remove scholars
ed.co <- gsub(',|A Banerjee|A. Banerjee|A. Banerjee.|A BANERJEE|E Duflo', "", ed_co_authors$authors)
# formatting to be prettier
ed.co <- gsub('\"|c|[()]|[...]', "", ed.co)
ed.co <- trimws(ed.co, which=c("both"))
ed.co <- paste(ed.co, collapse="&")
ed.co <- strsplit(ed.co, "&|  ")
ed.co <- ed.co[[1]]

# unique list of co-authors from Duflo's papers
ed.co <- unique(toupper(ed.co))
ed.co <- ed.co[ed.co != ""]
```

We use the statistical function intersect() to get the names of
intersecting co-authors between the two unique sets as found above.

``` r
friends <- intersect(ab.co, ed.co)
friends
```

    ##   [1] "R GLENNERSTER"      "C KINNAN"           "S COLE"            
    ##   [4] "L LINDEN"           "T BESLEY"           "AG CHANDRASEKHAR"  
    ##   [7] "MO JAKSON"          "M GHATAK"           "N QIAN"            
    ##  [10] "D KARLAN"           "J ZINMAN"           "R BANERJI"         
    ##  [13] "S KHEMANI"          "N GOLDBERG"         "R OSEI"            
    ##  [16] "W PARIENTÉ"         "J SHAPIRO"          "S GALIANI"         
    ##  [19] "S MULLAINATHAN"     "D KOTHARI"          "R HANNA"           
    ##  [22] "R PANDE"            "A DEATON"           "K MUNSHI"          
    ##  [25] "G POSTEL-VINAY"     "T WATTS"            "J LAFORTUNE"       
    ##  [28] "AP ZWANE"           "E VAN DUSEN"        "W PARIENTE"        
    ##  [31] "C NULL"             "E MIGUEL"           "K ROGOFF"          
    ##  [34] "R BENABOU"          "M BERTRAND"         "R HORNBEK"         
    ##  [37] "R CHATTOPADHYAY"    "P BARDHAN"          "K BASU"            
    ##  [40] "J BERRY"            "H KANNAN"           "S MUKERJI"         
    ##  [43] "M SHOTLAND"         "D KENISTON"         "N SINGH"           
    ##  [46] "E BREZA"            "M KREMER"           "S CHASSANG"        
    ##  [49] "D KENNISTON"        "C IMBERT"           "S MATHEW"          
    ##  [52] "S MUKHERJI"         "D AEMOGLU"          "S JOHNSON"         
    ##  [55] "S BARNHARDT"        "M JAKSON"           "M WALTON"          
    ##  [58] "JM GUERON"          "S ATHEY"            "GW IMBENS"         
    ##  [61] "CG KINNAN"          "E FIELD"            "A KHWAJA"          
    ##  [64] "JD ANGRIST"         "D CARD"             "C UDRY"            
    ##  [67] "B OLKEN"            "V ABHIJIT"          "E DUFIO"           
    ##  [70] "A DELAOURTE"        "WTD AEMOGLU"        "MG DASTIDAR"       
    ##  [73] "A FINKELSTEIN"      "SA COLE"            "JA ROBINSON"       
    ##  [76] "S RAJASEKARAN"      "J REYES"            "J PAN"             
    ##  [79] "JF ZAFF"            "AE DONLAN"          "M UNGAR"           
    ##  [82] "R ADAMS"            "P LILLRANK"         "B ABEL-SMITH"      
    ##  [85] "J AHERNE"           "A WHELTON"          "B BALKENHOL"       
    ##  [88] "GA AKERLOF"         "DAH ALAMGIR"        "B ARMENDARIZ"      
    ##  [91] "J MORDUH"           "TS RABIE"           "SN TOWFIGHIAN"     
    ##  [94] "C CLARK"            "M CAMMETT"          "C BAI"             
    ##  [97] "Q ZHANG"            "X FEI"              "J ZHAO"            
    ## [100] "M WANG"             "M ABRAMOVITZ"       "D AIGNER"          
    ## [103] "FB ABOAGYE"         "DA ROMÁN CEDILLO"   "R ABDULGANI"       
    ## [106] "I ABU BAKAR"        "J ROBINSON"         "R ABDELAL"         
    ## [109] "MR ABOUHARB"        "D CINGRANELLI"      "ME KONOMI"         
    ## [112] "MT DATTILO"         "M AKBULUT-YUKSEL"   "MB SHUSTER"        
    ## [115] "D DONALDSON"        "T ABDEL AZIZ"       "G BERG"            
    ## [118] "M ABREU"            "V MENDES"           "I BEN-DAVID"       
    ## [121] "M PEZZINI"          "AV BANERJEE"        "E DUFLO"           
    ## [124] "C JUMA"             "JL ROCCA"           "D RIEFF"           
    ## [127] "CO GRADA"           "H AARON"            "JT ABALUK"         
    ## [130] "JH ABBRING"         "J AMERIKS"          "C AZZI"            
    ## [133] "PB BAH"             "LC BAKER"           "D E ADENUTSI"      
    ## [136] "CRK AHORTOR"        "P AOSTA"            "C CALDERON"        
    ## [139] "P FAJNZYLBER"       "H LOPEZ"            "M CHAVAN"          
    ## [142] "DE ADENUTSI"        "PA AOSTA"           "EKK LARTEY"        
    ## [145] "FS MANDELMAN"       "P DIAMOND"          "J KOUNOUWEWA"      
    ## [148] "D CHAO"             "R ALMEIDA"          "P CARNEIRO"        
    ## [151] "E ARYEETEY"         "A ALESINA"          "RJ BARRO"          
    ## [154] "I MORRIS"           "D EMILSON ADENUTSI" "RH ADAMS JR"       
    ## [157] "J PAGE"             "C SNYDER"           "F MASHWAMA"        
    ## [160] "S BAIRD"            "J HIKS"             "WAW LEARNING"      
    ## [163] "H DAVID"            "JC AKER"            "IM MBITI"          
    ## [166] "JS PISHKE"          "AJ AUERBAH"         "M MOSTAGIR"        
    ## [169] "A OZDAGLAR"         "D LAIBSON"          "JA LIST"           
    ## [172] "S ANDERSON"         "M BAKER"            "J WURGLER"         
    ## [175] "E BASKER"           "S PARIS"            "M MARUANI"         
    ## [178] "M MERON"            "MG DUGGAN"          "RM MCLEARY"        
    ## [181] "G BEL"              "A GALEOTTI"         "BW ROGERS"         
    ## [184] "FZ AHMED"           "E WERKER"           "KB ANDERSON"       
    ## [187] "E DURBIN"           "A MIHAEL"

Q4: did the two publish together?

``` r
ab.together <- ab_co_authors %>% 
  filter(grepl('E Duflo|E DUFLO', as.character(researcher)))

papers_together <- ab.together$paperName

head(sort(papers_together))
```

    ## [1] ‘Health Care Delivery and Health Status in Udaipur District, Rajasthan                             
    ## [2] (Dis) organization and Success in an Economics MOOC                                                
    ## [3] (Measured) Profit is Not Welfare: Evidence from an Experiment on Bundling Microcredit and Insurance
    ## [4] 14.74 Foundations of Development Policy Syllabus                                                   
    ## [5] 14.74 Foundations of Development Policy, Spring 2004                                               
    ## [6] 14.771 Development Economics: Microeconomic Issues and Policy Models, Fall 2002                    
    ## 475 Levels: ¿ Cuál es tu evidencia? ...

``` r
# total number of papers co-authored together:
length(papers_together)
```

    ## [1] 186

Seeing how they worked together and how their research seems to be
similarly aligned from the previous analysis of word frequency, I
further examined the citations of the scholars’s works to see if there
is a great difference between them.

Q8: count the total number of citations for each journal.

We will aggregate the citations data based on the journal. I defined a
stand alone journal as a journal with a title disregarding it’s volume,
edition, year, or serial number.

``` r
# combine the two dfs of the two scholars
df.both <- rbind.data.frame(df.ab, df.ed)
#filter original df to get data that has citations, a journal, and strip the journal of numbers and punctuations (omit volume and year of journal for this purpose)
c <- df.both %>%
  select(paperName, journal, citations) %>%
  mutate("journal_main" = toupper(gsub('[[:punct:]]|[[:digit:]]', '', journal))) %>% 
  filter(!is.na(citations)) %>%
  filter(journal != "")

# use aggregate to count up citations
agg <- aggregate(c$citations, by=list("journal_main"=c$journal_main), FUN=sum)
colnames(agg) <- c("journal_main", "citations")

# order by decreasing number of citations
agg<- agg[with(agg, order(-citations)),]

head(agg)
```

    ##                                        journal_main citations
    ## 242          THE QUARTERLY JOURNAL OF ECONOMICS         26220
    ## 13                     AMERICAN ECONOMIC REVIEW          9698
    ## 134            JOURNAL OF ECONOMIC PERSPECTIVES          6530
    ## 204                                 PUBLIC AFFAIRS       5822
    ## 176           NATIONAL BUREAU OF ECONOMIC RESEARCH       4762
    ## 8   AMERICAN ECONOMIC JOURNAL APPLIED ECONOMICS          4640

Q9: From the citations data, which journal do you think is the most
influential in their academic field?

``` r
# first row will be the largest cited one
agg[1,]
```

    ##                               journal_main citations
    ## 242 THE QUARTERLY JOURNAL OF ECONOMICS         26220

The quarterly journal of economics seems to be the most influential with
26220 citations thus far. The second closest is the American Economic
Review at 9698, less than half the citations of the former.

Visually, this can be seen like so:

``` r
top_ten <- head(agg,10)
top_ten
```

    ##                                        journal_main citations
    ## 242          THE QUARTERLY JOURNAL OF ECONOMICS         26220
    ## 13                     AMERICAN ECONOMIC REVIEW          9698
    ## 134            JOURNAL OF ECONOMIC PERSPECTIVES          6530
    ## 204                                 PUBLIC AFFAIRS       5822
    ## 176           NATIONAL BUREAU OF ECONOMIC RESEARCH       4762
    ## 8   AMERICAN ECONOMIC JOURNAL APPLIED ECONOMICS          4640
    ## 139                JOURNAL OF POLITICAL ECONOMY          4461
    ## 220                                     SCIENCE          3442
    ## 131                  JOURNAL OF ECONOMIC GROWTH          2798
    ## 102                  HANDBOOK OF ECONOMIC GROWTH         2316

<img src="../images/top-ten.png" style="display: block; margin: auto;" />

That concludes my analysis of the two scholars. Further things to
explore would be the number of pages for each paper and the correlation
between pages an number of citations.

It would be interesting to also compare the other co-authors that were
found in the prior sections.
