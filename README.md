# Eresearch Elsevier ScienceDirect Consumer Service #


### Description
The purpose of this service is to consume the info which is provided from the
following elsevier api: https://api.elsevier.com/documentation/SCIDIRSearchAPI.wadl

The search view (result from the consuming of the service we get) 
is the following: http://api.elsevier.com/documentation/search/SCIDIRSearchViews.htm
we get the COMPLETE view.

See also:
*  http://api.elsevier.com/documentation/search/SCIDIRSearchTips.htm
*  http://api.elsevier.com/content/search/fields/scidir

The search query we use are the following:
* query=aus(fullname)
* query=aut(fullname)
* query=specific-author(fullname)


### External Dependencies needed in order to run service

* Academic VPN in order to fetch results from Elsevier API (https://dev.elsevier.com/documentation/ScienceDirectSearchAPI.wadl#d1e166)

* ActiveMQ
    * Execute: `docker-compose up`
    * Execute: `docker-compose down`


### Integration Tests (run docker-compose first)

* Execute: `mvn clean verify`


### Create Docker Image
* Execute: `mvn clean install -DskipITs=true`
* Execute: `docker build -t chriniko/eresearch-scidir-consumer:1.0 .` in order to build docker image.

* Fast: `mvn clean install -DskipITs=true && docker build -t chriniko/eresearch-scidir-consumer:1.0 .`


### How to run service (not dockerized)
* Execute: `docker-compose up`

* Two options:
    * Execute: 
        * `mvn clean install -DskipITs=true`
        * `java -jar -Dspring.profiles.active=dev target/eresearch-elsevier-sciencedirect-consumer-1.0-boot.jar`
                
    * Execute:
        * `mvn spring-boot:run -Dspring.profiles.active=dev`

* (Optional) When you finish: `docker-compose down`


### How to run service (dockerized)
* Uncomment the section in `docker-compose.yml` file for service: `eresearch-scidir-consumer:`

* Execute: `mvn clean install -DskipITs=true`

* Execute: `docker-compose build`

* Execute: `docker-compose up`

* (Optional) When you finish: `docker-compose down`



### Example Request

```json
{
  "firstname":"Anastasios",
  "initials":"",
  "surname":"Tsolakidis"
}

```



### Example Response

```json
{
    "operation-result": true,
    "process-finished-date": "2019-04-11T23:04:26.836Z",
    "requested-elsevier-sciencedirect-consumer-dto": {
        "firstname": "Anastasios",
        "initials": "",
        "surname": "Tsolakidis"
    },
    "fetched-results-size": 3,
    "fetched-results": [
        {
            "search-results": {
                "opensearch:totalResults": "5",
                "opensearch:startIndex": "0",
                "opensearch:itemsPerPage": "5",
                "opensearch:Query": {
                    "@role": "request",
                    "@searchTerms": "aus(Anastasios Tsolakidis)",
                    "@startPage": "0"
                },
                "link": [
                    {
                        "@_fa": "true",
                        "@href": "https://api.elsevier.com/content/search/sciencedirect?start=0&count=25&query=aus%28Anastasios+Tsolakidis%29&view=complete",
                        "@ref": "self",
                        "@type": "application/json"
                    },
                    {
                        "@_fa": "true",
                        "@href": "https://api.elsevier.com/content/search/sciencedirect?start=0&count=25&query=aus%28Anastasios+Tsolakidis%29&view=complete",
                        "@ref": "first",
                        "@type": "application/json"
                    }
                ],
                "entry": [
                    {
                        "@force-array": null,
                        "error": null,
                        "@_fa": "true",
                        "load-date": "2013-04-09T00:00:00Z",
                        "link": [
                            {
                                "@_fa": "true",
                                "@href": "https://api.elsevier.com/content/article/pii/S1877042813003789",
                                "@ref": "self",
                                "@type": null
                            },
                            {
                                "@_fa": "true",
                                "@href": "https://www.sciencedirect.com/science/article/pii/S1877042813003789?dgcid=api_sd_search-api-endpoint",
                                "@ref": "scidir",
                                "@type": null
                            }
                        ],
                        "prism:url": "https://api.elsevier.com/content/article/pii/S1877042813003789",
                        "dc:identifier": "DOI:10.1016/j.sbspro.2013.02.085",
                        "openaccess": "true",
                        "openaccessFlag": null,
                        "dc:title": "Institutional Research Management using an Integrated Information System",
                        "prism:publicationName": "Procedia - Social and Behavioral Sciences",
                        "prism:isbn": null,
                        "prism:issn": null,
                        "prism:volume": "73",
                        "prism:issueIdentifier": null,
                        "prism:issueName": null,
                        "prism:edition": null,
                        "prism:startingPage": "518",
                        "prism:endingPage": "525",
                        "prism:coverDate": "2013-02-27",
                        "prism:coverDisplayDate": null,
                        "dc:creator": "Tsolakidis Anastasios",
                        "authors": {
                            "author": [
                                {
                                    "$": "Tsolakidis Anastasios"
                                },
                                {
                                    "$": "Sgouropoulou Cleo"
                                },
                                {
                                    "$": "Papageorgiou Effie"
                                },
                                {
                                    "$": "Terraz Olivier"
                                },
                                {
                                    "$": "Miaoulis George"
                                }
                            ]
                        },
                        "prism:doi": "10.1016/j.sbspro.2013.02.085",
                        "pii": "S1877042813003789",
                        "pubType": null,
                        "prism:teaser": null,
                        "dc:description": null,
                        "authkeywords": null,
                        "prism:aggregationType": null,
                        "prism:copyright": null,
                        "scopus-id": null,
                        "eid": null,
                        "scopus-eid": null,
                        "pubmed-id": null,
                        "openaccessArticle": null,
                        "openArchiveArticle": null,
                        "openaccessUserLicense": null
                    },
                    {
                        "@force-array": null,
                        "error": null,
                        "@_fa": "true",
                        "load-date": "2013-04-15T00:00:00Z",
                        "link": [
                            {
                                "@_fa": "true",
                                "@href": "https://api.elsevier.com/content/article/pii/S002072921300177X",
                                "@ref": "self",
                                "@type": null
                            },
                            {
                                "@_fa": "true",
                                "@href": "https://www.sciencedirect.com/science/article/pii/S002072921300177X?dgcid=api_sd_search-api-endpoint",
                                "@ref": "scidir",
                                "@type": null
                            }
                        ],
                        "prism:url": "https://api.elsevier.com/content/article/pii/S002072921300177X",
                        "dc:identifier": "DOI:10.1016/j.ijgo.2013.03.004",
                        "openaccess": "false",
                        "openaccessFlag": null,
                        "dc:title": "Uterine manifestations of tuberous sclerosis complex as a random finding at laparoscopy",
                        "prism:publicationName": "International Journal of Gynecology & Obstetrics",
                        "prism:isbn": null,
                        "prism:issn": null,
                        "prism:volume": "122",
                        "prism:issueIdentifier": null,
                        "prism:issueName": null,
                        "prism:edition": null,
                        "prism:startingPage": "156",
                        "prism:endingPage": "157",
                        "prism:coverDate": "2013-08-31",
                        "prism:coverDisplayDate": null,
                        "dc:creator": "George Pados",
                        "authors": {
                            "author": [
                                {
                                    "$": "George Pados"
                                },
                                {
                                    "$": "Anastasios Makedos"
                                },
                                {
                                    "$": "Dimitrios Tsolakidis"
                                },
                                {
                                    "$": "Basil Tarlatzis"
                                }
                            ]
                        },
                        "prism:doi": "10.1016/j.ijgo.2013.03.004",
                        "pii": "S002072921300177X",
                        "pubType": null,
                        "prism:teaser": null,
                        "dc:description": null,
                        "authkeywords": null,
                        "prism:aggregationType": null,
          
          ...

```