indeed-client
=============

A command line client for the indeed.com job site.

This is a "mostly" complete implementation of the XML feed indeed.com API. Pull requests welcome :)

## Requirements

1. gradle
2. java sdk


## Build and Install

```
$ git clone https://github.com/digitalsanctum/indeed-client.git
$ cd indeed-client
$ gradle clean build
```

Add `indeed-client/build/indeed` to your `$PATH`


## Credits

This project was built with these excellent open source libraries:

1. [Google Guava](https://code.google.com/p/guava-libraries/)
2. [Retrofit](https://github.com/square/retrofit)
3. [Airline](https://github.com/airlift/airline)


## LICENSE

Copyright 2013 Shane Witbeck

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
