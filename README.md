# PDG-Core

PDG is a microservice query language for Java that makes easy to fetch information from multiple services in the most efficient manner.

In PDG you build queries expressing the fields and resources to fetch:

```java
Query query = pdg.queryBuilder()
        .get("user")
            .from("user")
            .with("id").value(2)
        .get("bio")
        .from("bio")
            .with("userId").chained(new String[]{"user", "id"})
        .get("allPosts")
            .from("posts")
            .timeout(5000)
            .with("userId").chained(new String[]{"user", "id"})
        .getQuery();

pdg.execute(query);
```

In the example above, first PDG will fetch all users and right after perform parallel requests to fetch users bios and posts.  

PDG is built upon the battle proven Clojure CSP and Http Kit to maximize throughput and performance.

### Motivation

When building a microservice architecture we often have to deal with services calling multiple services.

PDG does the heavy lifting of orchestrating and parallelizing the API calls, alleviating the burden of working with async requests in Java.

## Getting Started

### Installation
The best way to get started is to add the PDG dependency to your project file.

### Configuration
PDG receives a configuration class with the API mappings. You can use the available configuration repositories -- `SystemPropertiesConfigRepository`, `PropertiesFileConfigRepository` or `ClassConfigRepository` -- or implement your own, using the `ConfigRepository` interface.
The configuration must return a `RouteMap` object.

#### Resources

In PDG resources are the API urls to retrieve data. Examples of resource would be:

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

PDG pdg = new PDG(systemConfRepository);
```

#### Properties File

The class `PropertiesFileConfigRepository` will take a properties file and map the resources.
An example of properties file (e.g.: pdg.properties):

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
PropertiesFileConfigRepository propsFileConfigRepository = new PropertiesFileConfigRepository("resources/pdg.properties");

PDG pdg = new PDG(propsFileConfigRepository);
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

PDG pdg = new PDG(config);
```

#### Custom PDG Configuration

The default query option sets the debug to false. If you want to use debug on your queries you can instantiate a new PDG class with the debug options as follow:

```java
QueryOptions queryOptions = new QueryOptions();
queryOptions.setDebugging(true);
PDG pdg = new PDG(configRepository, queryOptions);
```
### Examples

#### Simple Query

Retrieving all films from Star Wars API

```java
PDG pdg = new PDG(new PropertiesFileConfigRepository("resources/pdg.properties"));
Query query = pdg.queryBuilder()
		.get("galaxyPlanets")
			.from("planets")
			.timeout(5000)
		.getQuery();
QueryResponse result = pdg.executeQuery(query);

// The JSON String
String jsonString = result.toString();

// The mapped object
List<Planet> planets = result.getList("galaxyPlanets", Planet.class);

```

#### Chained Query

Retrieving posts from a given user (id = 2), using chained parameters.

```java
PDG pdg = new PDG(new PropertiesFileConfigRepository("resources/pdg.properties"));
Query query = pdg.queryBuilder()
		.get("user")
			.from("user")
			.timeout(2000)
			.with("id")
				.value(2)
		.get("allPosts")
			.from("posts")
			.timeout(5000)
			.with("userId")
				.chained(new String[]{"user", "id"})
		.getQuery();
QueryResponse result = pdg.execute(query);

// The JSON String
String jsonString = result.toString();

// The mapped object
BlogUser user = result.get("user", BlogUser.class);
List<BlogPost> posts = result.getList("allPosts", BlogPost.class);
```
## Building From Source Code

As prerequisites to build PDG from source we have:

+ Java 8
+ Maven 3

Just clone this repo and run "mvn clojure:compile install".

## Contributing

Take a look at our contributing guidelines.

## License

Copyright © 2016 B2W Digital

Distributed under the MIT License.
