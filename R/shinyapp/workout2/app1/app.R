#' Workout 2, Stat 133, Jiyoo Jeong
#' Shiny App Returns on Investments
#' Inputs: 
#'    •Slider input forInitial Amount, from $0 to $10,000, in steps of $100. Default value of$1,000.
#'    •Slider input forAnnual Contribution, from $0 to $5,000, in steps of $100. Default valueof $200.
#'    •Slider input forAnnual Growth Ratein percentage, from 0% to 20%, in steps of 0.1%.Default value of 2%.
#'    •Slider input forHigh  Yield  ratein percentage, from 0% to 20%, in steps of 0.1%.Default value of 2%.
#'    •Slider input forFixed Income rate(U.S. Bonds) in percentage, from 0% to 20%, insteps of 0.1%. Default value of 5%.
#'    •Slider input forUS Equity rate(U.S. Stocks) in percentage, from 0% to 20%, in stepsof 0.1%. Default value of 10%.
#'    •Slider input forHigh Yield volatilityin percentage, from 0% to 20%, in steps of 0.1%.Default value of 0.1%.
#'    •Slider input forFixed Income volatility(U.S. Bonds) in percentage, from 0% to 20%,in steps of 0.1%. Default value of 4.5%.
#'    •Slider input forUS Equity volatility(U.S. Stocks) in percentage, from 0% to 20%, insteps of 0.1%. Default value of 15%.
#'    •Slider input forYears, from 0 to 50, in steps of 1. Default value of 20.
#'    •Numeric input forRandom Seed, this is the value to be passed toset.seed(). Default value of 12345. To be used for generating new runs of random numbers.
#'    •Select input forFacet?. Choices: “Yes”, and “No”. Default value “Yes”.
#'    
#' Outputs:
#'    Reactive timeline plot of different modes of investment based on inputs.

library(shiny)

# Define UI for application that draws a histogram
ui <- fluidPage(

    # Application title
    titlePanel("Returns on Investments"),

    # Sidebar with a slider input for number of bins 
    fluidRow(
        column(3,
            sliderInput("initial",
                        label = "Initial Amount:",
                        min = 0,
                        max = 10000,
                        value = 1000,
                        step= 100),
            sliderInput("annual_contribution",
                        label = "Annual Contribution:",
                        min = 0,
                        max = 5000,
                        value = 200,
                        step= 100),
            sliderInput("annual_growth",
                        label = "Annual Growth: (in %)",
                        min = 0,
                        max = 20,
                        value = 2,
                        step= .1)
        ),
        
        column(3,
            sliderInput("high_yield_rate",
                        label = "High Yield Rate: (in %)",
                        min = 0,
                        max = 20,
                        value = 2,
                        step= .1),
            sliderInput("fixed_income_rate",
                        label = "Fixed Income Rate: (in %)",
                        min = 0,
                        max = 20,
                        value = 5,
                        step= .1),
            sliderInput("us_equity_rate",
                        label = "US Equaity Rate: (in %)",
                        min = 0,
                        max = 20,
                        value = 10,
                        step= .1)
        ),
        
        column(3,
            sliderInput("high_yield_volatility",
                        label = "High Yield Volatility: (in %)",
                        min = 0,
                        max = 20,
                        value = .1,
                        step= .1),
            sliderInput("fixed_income_volatility",
                        label = "Fixed Income Volatility: (in %)",
                        min = 0,
                        max = 20,
                        value = 4.5,
                        step= .1),
            sliderInput("us_equity_volatility",
                        label = "US Equity Volatility: (in %)",
                        min = 0,
                        max = 20,
                        value = 15,
                        step= .1)
        ),
        
        column(3,
            sliderInput("year",
                        label = "Years:",
                        min = 0,
                        max = 50,
                        value = 20,
                        step= 1),
            numericInput("seed",
                        label = "Choose a random seed.",
                        value = 12345),
            selectInput("facet",
                        label = "Facet?",
                        choices = c("Yes", "No"),
                        selected="Yes")
        ),

        # main panel
        mainPanel(
            plotOutput("plot")
        )
    )
    
)

# Define server logic required to draw a timeline plot
server <- function(input, output) {
    output$plot <- renderPlot({
        library(dplyr)
        library(ggplot2)
        library(reshape2)
        
        set.seed(input$seed)
        # x axis is years
        # y axis is total moneys
        # repeated with initial, recursive amt(1+r) + ann_cont(1+g)^year-
        y_hy <- c(input$initial)
        y_usb <- c(input$initial)
        y_uss <- c(input$initial)
        
        #' name: recursive_it
        #' description: recursively fills in the cumulative amount of investment over the tree investment types
        #' input: year
        #' output: none.
        recursive_it <- function(year, y_h, y_sb, y_ss){
            if (year<=input$year){
                # print(year)
                g <- input$annual_growth
                c <- input$annual_contribution
                r_hy <- rnorm(1, input$high_yield_rate, input$high_yield_volatility)
                r_usb <- rnorm(1, input$fixed_income_rate, input$fixed_income_volatility)
                r_uss <- rnorm(1, input$us_equity_rate, input$us_equity_volatility)
                # print(y_h[year-1])
                y_h[year] <- y_h[year-1]*(1+r_hy/100) + c*(1+g/100)^(year-1)
                y_sb[year] <- y_sb[year-1]*(1+r_usb/100) + c*(1+g/100)^(year-1)
                y_ss[year] <- y_ss[year-1]*(1+r_uss/100) + c*(1+g/100)^(year-1)
                
                recursive_it(year+1, y_h, y_sb, y_ss)
            } else {
                years <- 1:input$year -1
                return(data.frame("years" = years,
                                  "high_yield" = y_h, 
                                  "fixed_income" = y_sb, 
                                  "equity" = y_ss
                                  ))
            }
        }
        
        # data frame from calling recursive_it
        dat <- recursive_it(2, y_hy, y_usb, y_uss)
        # melt to group by columns
        dat2 <- melt(dat, id.vars= c("years"), variable.name = "columns", value.name = "returns")
        
        # plot!
        if (input$facet=="Yes"){
            ggplot(data = dat2, 
                   aes(x=years, y=returns, group=columns, col=columns)) +
                geom_path() +
                geom_point() +
                ggtitle("Relative frequencies of number of blue balls") +
                theme_bw() +
                facet_grid(~columns)
        }else{
            ggplot(data = dat2, 
                   aes(x=years, y=returns, group=columns, col=columns)) +
                geom_path() +
                geom_point() +
                ggtitle("Relative frequencies of number of blue balls") +
                theme_bw() 
        }
    })
}

# Run the application 
shinyApp(ui = ui, server = server)
