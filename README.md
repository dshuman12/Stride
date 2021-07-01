# STRIDE

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
How do I get across the city? Like actually? What will my trip look like? Stride is a mobility as a service application, where all modes of transport are viable options. The context matters :) 
Stride allows you to not only appreciate the destination, but also the journey. 

API Requirements: MTA API, Citi Bikes API, Open Street Maps API, Open Sidewalk API, Google Maps / Mapbox
The app will begin with New York City


### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Navigation
- **Mobile:** Mobile is used for the entire app experience.
- **Story:** User can search for a destination location and the app will route them there via the most optimal path. 
- **Market:** Typically for young, urban dwellers.
- **Habit:** Any trip can be routed via Stride, so a typical user would use Stride on a daily basis. 
- **Scope:** V1 would allow a simple routing system based solely off of walking. V2 would include other APIs for routing to optimize the algorithm. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can see a map of their nearby area (Google Maps API)
* User can search locations in map (Google Maps API)
* User is routed to that location by walking
* User is routed via an optimized algorithm that includes City services (transit, bikes)
* User is notified of sidewalk condition / bus conditions

**Optional Nice-to-have Stories**

* User can select "context" settings
    * Building History
    * Civic Duty (voting booths)
    * Parks + Greenspace
    * Food (https://www.programmableweb.com/api/street-food-app)
    * Cultural centers 
    * Sidewalk conditions
    * Crowds/people
    * Hills/incline
    * Weather
    * Health
    * Kid-friendly (schools, gun rates)
* User can select preferences for type of movement (ex. if they have access to a bike/car)
* User can get a special navigation treatment during their trip (podcast recommendations, music recommendations)
* User gets specific notifications given their trip (weather related, etc.)

### 2. Screen Archetypes

* Map
   * User can search for a location
   * User will get routed to that location
* Preferences
   * User can select preferences for type of movement
   * User can select what kind of movement they have access to
   * User can select their area context
* Settings
   * User can select a context for their map type
   * User can select their location

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map Screen
* Preferences
* Settings

**Flow Navigation** (Screen to Screen)

* Map 
    * Preferences
    * Settings
* Settings
    * Preferences
    * Back to Home
* Preferences
    * Back to Home

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
