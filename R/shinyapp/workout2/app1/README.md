
# README.md for app.R
## author: Jiyoo Jeong
- Github username: jiyooj
- Email: jiyooj [at] email.com
- Lab section: 107
- GSI: Cindy Zhang
### date: 11/12/2019

## Shiny App Returns on Investments

This Shiny App takes in several variants to display a timeline graph of the cumulative investment over time. 

## Inputs: 

- Slider input for Initial Amount, from $0 to $10,000, in steps of $100. Default value of$1,000.
- Slider input for Annual Contribution, from $0 to $5,000, in steps of $100. Default value of $200.
- Slider input for Annual Growth Ratein percentage, from 0% to 20%, in steps of 0.1%. Default value of 2%.
- Slider input for High  Yield  ratein percentage, from 0% to 20%, in steps of 0.1%. Default value of 2%.
- Slider input for Fixed Income rate(U.S. Bonds) in percentage, from 0% to 20%, insteps of 0.1%. Default value of 5%.
- Slider input for US Equity rate(U.S. Stocks) in percentage, from 0% to 20%, in stepsof 0.1%. Default value of 10%.
- Slider input for High Yield volatilityin percentage, from 0% to 20%, in steps of 0.1%. Default value of 0.1%.
- Slider input for Fixed Income volatility(U.S. Bonds) in percentage, from 0% to 20%,in steps of 0.1%. Default value of 4.5%.
- Slider input for US Equity volatility(U.S. Stocks) in percentage, from 0% to 20%, insteps of 0.1%. Default value of 15%.
- Slider input for Years, from 0 to 50, in steps of 1. Default value of 20.
- Numeric input for Random Seed, this is the value to be passed toset.seed(). Default value of 12345. To be used for generating new runs of random numbers.
- Select input for Facet?. Choices: “Yes”, and “No”. Default value “Yes”.
    
## Outputs:
Reactive timeline plot of different modes of investment based on inputs as a Shiny App.
