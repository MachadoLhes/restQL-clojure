# PDG-Core

Microservice query language for Java

PDG is a microservice query language that orchestrates and parallelize multiple API calls. It's built upon the battle proved Clojure CSP and Http Kit to maximize throughput and performance.

In PDG you build queries expressing the fields and resources to fetch. Example:

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

In this case, first PDG will fetch all users and right after perform parallel requests to fetch users bios and posts.  

### When to use PDG

If your project performs multiple requests to different API urls and you need to match information from a previous call into the next ones, you should definitely give PDG a try.

Instead of making one call, waiting the results and then making another bunch of other calls using the received results, you can make just one graph call and receive all the data you need.

## Getting Started

### Installation

The best way to get started is to add the PDG dependency to your project file.

#### Maven

Add the dependency to your pom.xml

```xml
<dependency>
  <groupId>com.b2wdigital</groupId>
  <artifactId>pdg-core</artifactId>
  <version>${pdg-core-version}</version>
</dependency>
```

#### Gradle

Add the dependency to your build.gradle

```gradle
dependencies {
	compile 'com.b2wdigital:pdg-core:VERSION'
}
```

### Configuration

PDG receives a configuration class with the API mappings. You can use the available configuration repositories -- `SystemPropertiesConfigRepository`, `PropertiesFileConfigRepository` or `ClassConfigRepository` -- or implement your own, using the `ConfigRepository` interface.

The configuration must return a `RouteMap` object.

#### Resources

On PDG resources are the API urls to retrieve data. Examples of resource would be:

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
```

## Building From Source Code

As prerequisites to build PDG from source we have:

+ Java 8
+ Clojure
+ Clojure Boot

We use Clojure Boot to build PDG. If you're not familiar with Clojure or Boot, please take a look at [Clojure Boot GitHub Repository](https://github.com/boot-clj/boot).

## Contributing

Take a look at our contributing guidelines.

## License

Copyright © 2016 B2W Digital

Distributed under the MIT License.