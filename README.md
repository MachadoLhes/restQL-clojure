[![Build Status](https://travis-ci.org/B2W-BIT/restQL-core.svg?branch=master)](https://travis-ci.org/B2W-BIT/restQL-core)
# restQL-Core

restQL-core allows you to run restQL queries directly from JVM applications, making easy to fetch information from multiple services in the most efficient manner. e.g.:

```
from search
    with
        role = "hero"

from hero as heroList
    with
        name = search.results.name
```

You can learn more about restQL query language [here](https://github.com/B2W-BIT/restQL-server/wiki/RestQL-Query-Language) and [here](http://restql.b2w.io)

restQL-core is built upon the battle proven [HttpKit](http://www.http-kit.org/600k-concurrent-connection-http-kit.html) and [Clojure core.async](http://clojure.com/blog/2013/06/28/clojure-core-async-channels.html) to maximize performance and throughtput.

If you're using Java you may want to check [restQL-core-java](https://github.com/B2W-BIT/restQL-core-java) or [restQL-Server](https://github.com/B2W-BIT/restQL-server) if you're using another languagem or working in a client application.

## Getting Started

### Installation

Add restQL dependency to your project

**Lein**

```
[b2wdigital/restql-core "2.2"]
```

### First query

```clojure
(require '[restql.core.api.restql :as restql])
(restql/execute-query :mappings { :user "http://your.api.url/users/:name" } :query "from user with name = $name" :params { :name "Duke Nukem" } )
```

In the example above restQL will call user API passing "Duke Nukem" in the name param.

```

## Building From Source Code

As prerequisites to build restQL from source we have:

+ Java 8
+ Leiningen 2.x

Just clone this repo and run "lein jar".

## License

Copyright © 2016 B2W Digital

Distributed under the MIT License.
