# java-explore-with-me
Template repository for ExploreWithMe project. [Link](https://github.com/efabritz-wd/java-explore-with-me/pull/3)

## ExploreWithMe

Yandex practicum diploma project. Events board implementation. Allow users to share information about interesting events and to participate in them.

## Two Services

- The main service contains everything necessary for the product's operation;
- The statistics service stores the number of views and allows making various selections for analyzing the application's performance.

### Main Service

Contains three logical parts:

- The public part is available without registration to any network user, provides search and filtering capabilities for events;
- The private part is available only to authorized users. Viewing, adding, editing of events;
- The administrative part—for service administrators. Viewing, adding, editing, deletion, moderation: confirmation, rejection, activation of events.

### Statistics Service

Collects information about the number of user requests to the event lists and the number of requests for detailed information of specific events. 
