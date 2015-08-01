
## Prepare 

	- Install and run mongo at localhost:27017
  	- Import data into histxn.tx
	- git clone https://github.com/tjworks/tjpoc 	
  	- cd tjpoc
  	
## Build & Test Instruction(optional):
 	
 	- Install JDK, Maven	
  	- mvn clean compile assembly:single
  	- mvn test 

## Demo  from command line: 

  ./run.sh  -p key=value -p key=value

## Parameters for run.sh
	
#### Query paramegers

	- account
	- from_date
	- to_date
	- trace 
	- min_amount
	- max_amount
	- ccy

#### Mongo connection params
	- url  #  MongoDB connection URL, default: mongodb://localhost:27017
	- db   #  default to histxn
	- collection  # default to tx


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


## Loadrunner Integration

- Add jar file(under target/ folder) to the load runner path

```
	import com.mongodb.pinganpoc.MongoPoc;
	...
	public void init(){

		MongoPoc.init("mongodb://localhost:27017", "histxn", "tx");

	}
  	public void action(){
  		...
  		String res = MongoPoc.runQuery(
            new Long("272100088551"), 		// account
            "2015-07-01",					// from date
            "2015-07-01",					// to date
            "RMB",							// ccy
            new Double(100000),				// min amount
            new Double(200000), 			// max amount
            null							// host trace
        );   
  		...
  	} 
  ```

Please refer to https://github.com/tjworks/tjpoc/blob/master/src/test/java/com/mongodb/pinganpoc/MongoPocTest.java for more example usage from Java. 
