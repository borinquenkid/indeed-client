indeed-client
=============

A command line client for the indeed.com job site.

This is a "mostly" complete implementation of the XML feed indeed.com API. Pull requests welcome :)


## Build and Install

```
$ git clone https://github.com/digitalsanctum/indeed-client.git
$ cd indeed-client
$ gradle clean build
```

1. Add `indeed-client/build/indeed` to your `$PATH`
2. Create a file `~/.indeed` with your indeed publisher id or use the `-id` arg with each command.


## Examples

Search for `java` in `portland, or` and sort by `date`:

```
digitalsanctum:~ shane$ indeed search java -loc 'portland, or' -sort date

Showing 1-10 of 709 results sorted by date. [query='java', location='portland, or']
----------------------------------------------------------------------------------------------------------------------------------
job_title                     company                       location                      posted              job_key
----------------------------------------------------------------------------------------------------------------------------------
Lead IT Technology Content ...Nike                          Portland, OR                  6 hours ago         9995ccd7117845e2
Senior IT Technology Conten...Nike                          Portland, OR                  6 hours ago         c8e3e0980ef53e26
Software Engineer             Lexicon Staffing Inc          Portland, OR 97201            9 hours ago         bf8a9d1cc7c5d8e7
9702 - .Net Software DeveloperWebMD                         Portland, OR 97210            9 hours ago         a59592d44609214a
Software Engineer             Cypress Semiconductor Corpo...Beaverton, OR                 11 hours ago        9a472d1cf5d61d4c
Principal Software Engineer...UTi Worldwide                 Portland, OR                  12 hours ago        77a5c471ff154df9
Software Engineer - JAVA      UTi Worldwide                 Portland, OR                  12 hours ago        2b867426ce898e55
Senior Software Engineer - ...UTi Worldwide                 Portland, OR                  12 hours ago        61170a2cb4265fbb
Android Developer             CorSource                     Portland, OR                  13 hours ago        a6ac0578eef384ea
Software Programmer           CorSource                     Portland, OR                  13 hours ago        ffa9074660eb1dae
```

Get more detail about a specific job by the `job_key`:

```
digitalsanctum:~ shane$ indeed detail 9995ccd7117845e2
```

Open a job in a browser:

```
digitalsanctum:~ shane$ indeed open 9995ccd7117845e2
opening job 9995ccd7117845e2 (url=http://www.indeed.com/rc/clk?jk=9995ccd7117845e2&atk=) in browser...
```


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
