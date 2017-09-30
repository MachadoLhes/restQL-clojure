[![Build Status](https://travis-ci.org/B2W-BIT/restQL-core.svg?branch=master)](https://travis-ci.org/B2W-BIT/restQL-core)
# restQL-Core

restQL-core allows you to run restQL queries directly from JVM applications, making easy to fetch information from multiple services in the most efficient manner.

```
from search
    with
        role = "hero"

from hero as heroes
    with
        name = search.results.name
```

More about restQL query language [here](https://github.com/B2W-BIT/restQL-server/wiki/RestQL-Query-Language) and [here](http://restql.b2w.io)

If you're using another languagem or working in a client application you may want to check  [restQL-Server](https://github.com/B2W-BIT/restQL-server).

# Getting Started

## Installation
Add restQL dependency to your project

```xml
<dependency>
	<groupId>com.b2wdigital</groupId>
        <artifactId>restql-core</artifactId>
       	<version>2.0.0</version>
</dependency>
```

## Installation

```java
ClassConfigRepository config = new ClassConfigRepository();
config.put("user", "http://your.api.url/users/:id");

RestQL restQL = new RestQL(config);
String query = "from user params id = ?";

QueryResponse response = restql.executeQuery(query, 1);
```

In the example above, first restQL will fetch all users and right after perform parallel requests to fetch users bios and posts.  

restQL is built upon the battle proven Clojure CSP and Http Kit to maximize throughput and performance.

### Configuration
RestQL receives a configuration class with the API mappings. You can use the available configuration repositories -- `SystemPropertiesConfigRepository`, `PropertiesFileConfigRepository` or `ClassConfigRepository` -- or implement your own, using the `ConfigRepository` interface.
The configuration must return a `RouteMap` object.

#### Resources

In restQL resources are the API urls to retrieve data. Examples of resource would be:

+ planets: http://swapi.co/api/planets/
+ planet: http://swapi.co/api/planets/:id

Everytime you need an url route parameter, use the parameter name with `:` to reference to that path variable, as we can see on the `planet` resource above.

#### System Properties

The class `SystemPropertiesConfigRepository` searches resources through the JVM arguments.
An example of JVM args configuration is:

```
[Other JVM arguments...]
-Dplanets=http://swapi.co/api/planets/
-Dplanet=http://swapi.co/api/planets/:id
```

To use the class:

```java
SystemPropertiesConfigRepository systemConfRepository = new SystemPropertiesConfigRepository();

RestQL restql = new RestQL(systemConfRepository);
```

#### Properties File

The class `PropertiesFileConfigRepository` will take a properties file and map the resources.
An example of properties file (e.g.: restql.properties):

```properties
#### Star Wars API ####
# Get all
planets=http://swapi.co/api/planets/
people=http://swapi.co/api/people/object()
films=http://swapi.co/api/films/
# Get one
planet=http://swapi.co/api/planets/:id
person=http://swapi.co/api/people/:id
film=http://swapi.co/api/films/:id
```

To use the class:

```java
PropertiesFileConfigRepository propsFileConfigRepository = new PropertiesFileConfigRepository("resources/restql.properties");

RestQL restql = new RestQL(propsFileConfigRepository);
```

#### Java Class

The class `ClassConfigRepository` provides a class based Repository to configure resources directly using Java code.
An example of configuration using a Java class:

```java
ClassConfigRepository config = new ClassConfigRepository();
config.put("forces", "https://data.police.uk/api/forces");
config.put("force", "https://data.police.uk/api/forces/:forceid");
config.put("neighbourhoods", "https://data.police.uk/api/:forceid/neighbourhoods");
config.put("crimes", "https://data.police.uk/api/crimes-no-location");
config.put("crimeDetails", "https://data.police.uk/api/outcomes-for-crime/:persistent_id");

RestQL restql = new RestQL(config);
```

#### Custom restQL Configuration

The default query option sets the debug to false. If you want to use debug on your queries you can instantiate a new restQL class with the debug options as follow:

```java
QueryOptions queryOptions = new QueryOptions();
queryOptions.setDebugging(true);
RestQL restql = new RestQL(configRepository, queryOptions);
```
### Examples

#### Simple Query

Retrieving all films from Star Wars API

```java
ClassConfigRepository config = new ClassConfigRepository();
config.put("cards", "http://api.magicthegathering.io/v1/cards");

RestQL restQL = new RestQL(config);

String query = "from cards as cardslist params type = ?";

QueryResponse response = restQL.executeQuery(query, "Artifact");

// The JSON String
String jsonString = response.toString();

// The mapped object
List<MTGCard> cards = result.getList("cardslist", MTGCard.class);
```

#### Chained Query

Retrieving posts from a given user (id = 2), using chained parameters.

```java
ClassConfigRepository config = new ClassConfigRepository();
config.put("cards", "http://api.magicthegathering.io/v1/cards");
config.put("card", "http://api.magicthegathering.io/v1/cards/:id");

RestQL restQL = new RestQL(config);

String queryCardsAndDetails = "from cards as cardsList params type = ? \n"
        + "from card as cardWithDetails params id = cardsList.id";

QueryResponse response = restQL.executeQuery(query, "Artifact");

// The JSON String
String jsonString = response.toString();

// The mapped object
List<MTGCard> cards = result.getList("cardWithDetails", MTGCard.class);
```
## Building From Source Code

As prerequisites to build restQL from source we have:

+ Java 8
+ Maven 3

Just clone this repo and run "mvn compile install".

## License

Copyright © 2016 B2W Digital

Distributed under the MIT License.
