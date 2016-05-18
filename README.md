[![Build Status](https://travis-ci.org/julianovidal/flume-ng-redis.svg?branch=master)](https://travis-ci.org/julianovidal/flume-ng-redis)

# Redis Sink Plugin for Apache Flume NG

This is an [Apache Flume](https://flume.apache.org/) Skink plugin to be used on top of [Redis](http://redis.io)

The latest build was created on top of Apache Flume v1.6.0
  
## Current Dependencies
[Jedis](https://github.com/xetorthio/jedis) v2.7.2

[Apache Commons Pool2](http://commons.apache.org/proper/commons-pool/) v2.3

## Building yourself

`git clone git@github.com:julianovidal/flume-ng-redis.git`

`cd flume-ng-redis`

`mvn clean test package`

## How do I use it?
You can download the latest build at: http://github.com/julianovidal/flume-ng-redis/releases

or build yourself

Add the libraries to Apache Flume plugin.d folder
 
`cp target/flume-ng-redis-1.0.0.jar $FLUME_HOME/plugins.d/flume-ng-redis/lib/`

Copy the Jedis library and the Commons Pool 2 library

`cp ~/.m2/repository/redis/clients/jedis/2.7.2/jedis-2.7.2.jar $FLUME_HOME/plugins.d/flume-ng-redis/libext/`

`cp ~/.m2/repository/org/apache/commons/commons-pool2/2.3/commons-pool2-2.3.jar $FLUME_HOME/plugins.d/flume-ng-redis/libext/`


## License
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

`http://www.apache.org/licenses/LICENSE-2.0`

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.