
## Build & Test Instruction:

 	- install and run mongo at localhost:27017
  	- import data into histxn.tx
 	- Install JDK, Maven
	- git clone https://github.com/tjworks/tjpoc
  	- cd tjpoc
  	- mvn clean compile assembly:single
  	- mvn test 

 ## Demo from command line: 
  
  	- ./run.sh 

## Parameters for run.sh
	
	- account
	- from_date
	- to_date
	- trace 
	- min_amount
	- max_amount
	- ccy

## Sample run


```
  ./run.sh -p account=11000253321701 -p from_date=2015-07-01 -p to_date=2015-07-01  
```

 ```
  ./run.sh -p account=11000253321701 -p from_date=2015-07-01 -p to_date=2015-07-01  -p trace=159960
```

```
  ./run.sh -p account=11000253321701 -p from_date=2015-07-01 -p to_date=2015-07-01  -p min_amount=304 -p max_amount=2000
```
 
 ```
 ./run.sh -p account=272100088551 -p from_date=2015-07-01 -p to_date=2015-08-30  -p min_amount=100000 -p max_amount=200000
```
