[![Build Status](https://travis-ci.org/B2W-BIT/restQL-core.svg?branch=master)](https://travis-ci.org/B2W-BIT/restQL-core)
# restQL-Core

restQL-core allows you to run restQL queries directly from JVM applications, making easy to fetch information from multiple services in the most efficient manner. If you want to query making HTTP calls from any client check [restQL-server](https://github.com/B2W-BIT/restQL-core):

```
from search
    with
        role = "hero"

from hero as heroList
    with
        name = search.results.name
```

Links

* [restql.b2w.io](http://restql.b2w.io): Project home page,
* [game.b2w.io](http://game.b2w.io): A game developed to teach the basics of restQL language,
* [restQL-server](https://github.com/B2W-BIT/restQL-core): The main restQL repo. It spin up a restQL-server that will listen for queries. Can be used by any language/project.
* [restQL-core-java](https://github.com/B2W-BIT/restQL-core-java): If you want to embed restQL directly into your Java application,
* [restQL-manager](https://github.com/B2W-BIT/restQL-manager): To manage saved queries and resources endpoints. restQL-manager requires a MongoDB instance.
* [Tackling microservice query complexity](https://medium.com/b2w-engineering/restql-tackling-microservice-query-complexity-27def5d09b40): Project motivation and history
* [Wiki](https://github.com/B2W-BIT/restQL-server/wiki/RestQL-Query-Language): Project documentation.

Who're talking about restQL

* [infoQ: restQL, a Microservices Query Language, Released on GitHub](https://www.infoq.com/news/2018/01/restql-released)
* [infoQ: 微服务查询语言restQL已在GitHub上发布](http://www.infoq.com/cn/news/2018/01/restql-released)
* [OSDN Mag: マイクロサービスクエリ言語「restQL 2.3」公開](https://mag.osdn.jp/18/01/12/160000)

## Getting Started

### Installation

Add restQL dependency to your project

**Lein**

```
[b2wdigital/restql-core "2.3"]
```

### First query

```clojure
(require '[restql.core.api.restql :as restql])
(restql/execute-query :mappings { :user "http://your.api.url/users/:name" } :query "from user with name = $name" :params { :name "Duke Nukem" } )
```

In the example above restQL will call user API passing "Duke Nukem" in the name param.

## Building From Source Code

As prerequisites to build restQL from source we have:

+ Java 8
+ Leiningen 2.x

Just clone this repo and run "lein jar".

## License

Copyright © 2016 B2W Digital

Distributed under the MIT License.
