####
# title: functions.R
# author: Jiyoo Jeong
# date: 12/06/2019
# description: This script creates the graphical material required for the report.
####

library(wordcloud)
library(dplyr)
library(ggplot2)
library(readr)

# load the data frames from the report
load("workout3.RData")
getwd()
# path to file
path = "/Users/jiyoojeong/Desktop/stat133/workouts-jiyoojeong/workout3/"

#### DATA VIS
# word clouds

png(filename = paste0(path, "images/wordcloud_banerjee.png"))
wordcloud(w.count$word, 
          freq=w.count$count, 
          random.color = TRUE, 
          colors=c("red", "light green", "blue", "pink", "yellow", "gray", "purple", "orange"))
dev.off()


png(filename = paste0(path, "images/wordcloud_duflo.png"))
wordcloud(w.b.count$word, 
          freq=w.b.count$count, 
          random.color = TRUE, 
          colors=c("red", "light green", "blue", "pink", "yellow", "gray", "purple", "orange"))
dev.off()


# line plots

#ab
ab_year <- df.ab$year

pub.a.freq <- table(ab_year)
pub.a.freq <- cbind.data.frame("year" = names(pub.a.freq), 
                               "count" = as.integer(pub.a.freq), 
                               stringsAsFactors=FALSE)

png(filename = paste0(path, "images/publications_banerjee.png"))
ggplot(pub.a.freq, aes(x=year, y=count, group=1)) +  
  geom_line(color="light blue") + 
  theme_bw() + 
  theme(axis.text.x = element_text(angle=-90, hjust=1)) + 
  labs(title="Publications Over Time", 
       subtitle="A. Banerjee",
       caption="Publication frequency of Abjijit Banerjee over time.\n Peak Number of publications are in 2012.")
dev.off()


#ed
ed_year <- df.ed$year

pub.b.freq <- table(ed_year)
pub.b.freq <- cbind.data.frame("year" = names(pub.b.freq), 
                               "count" = as.integer(pub.b.freq), 
                               stringsAsFactors=FALSE)

png(filename = paste0(path, "images/publications_duflo.png"))
ggplot(pub.b.freq, aes(x=year, y=count, group=1)) +  
  geom_line(color="pink") + 
  theme_bw() + 
  theme(axis.text.x = element_text(angle=-90, hjust=1)) + 
  labs(title="Publications Over Time", 
       subtitle="E. Duflo",
       caption="Publication frequency of Esther Duflo over time.\n Peak Number of publications are in 2012.")
dev.off()

# words over time

#' @name: pop_words()
#' @desc: determines the word count of the predetermined popular words in paper titles
#' @param: df - the data frame of an author where the punctuations are removed. 
#'         df must have columns 'removedName' and 'year'
#' @param: name (character) - the name of the researcher.
#' @output: a ggplot line graph with the frequency of the words.
pop_words <- function(df, name) {
  wording <- select(df, removedName, year)
  wc <- rbind.data.frame(
    cbind("year" = wording$year,
          "w" ="india",
          "count" = str_count(tolower(wording$removedName), "\\b(india)\\b")),
    cbind("year" = wording$year,
          "w" ="economics",
          "count" = str_count(tolower(wording$removedName), "\\b(economics)\\b")),
    cbind("year" = wording$year,
          "w" ="development",
          "count" = str_count(tolower(wording$removedName), "\\b(development)\\b")), 
    cbind("year" = wording$year,
          "w" = "evidence",
          "count" = str_count(tolower(wording$removedName), "\\b(evidence)\\b")),
    stringsAsFactors = FALSE)
  
  wc <- mutate(wc, "count"=as.integer(count), "year" = as.integer(year))
  head(wc, 5)
  
  wc <- wc %>% group_by(year, w) %>%
    summarise(count= sum(count))
  wc <- as.data.frame(wc)
  head(wc)
  
  plot <- wc %>% ggplot(aes(x=year, y=count, group=w)) +  
    geom_point(aes(color=w)) +
    geom_line(aes(color=w), alpha=.2) +
    theme_bw() +
    ylab("counts") +
    ggtitle(paste("The Frequencies of popular words from", name)) +
    facet_grid(w~.)
  
  return(plot)
}



png(filename = paste0(path, "images/wc_banerjee.png"))
pop_words(df.removed.ab, "A. Banerjee")
dev.off()

png(filename = paste0(path, "images/wc_duflo.png"))
pop_words(df.removed.ed, "E. Duflo")
dev.off()


### top ten citations



png(paste0(path, "images/top-ten.png"))
ggplot(top_ten, aes(x=journal_main, y=citations, color=citations)) + 
  geom_point() +
  theme_bw() +
  theme(axis.text.x = element_text(angle=-90, hjust=0, size=7, vjust=0)) +
  ggtitle("number of citations of the top ten economic journals")
dev.off()



