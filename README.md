# ClassWatcher
This is an application that is used to notify users when a class theyve requested becomes available. Designed for Florida State University, but should be easily portable for any college that uses collegescheduler.com. In early stages of development, right now only supports Discord as a notification client. 

# Usage

Run the appAuth subproject with appropriate settings in the app.properties file. (NOTE: You MUST define the location of a Selenium WebDriver! Right now, the only one supported is firefox) All necessary chars must be escaped with a backslash

Run the api subproject with appropriate settings in the app.properties file. (Need a username and password for authentication, and a discord bot token to do anything useful)

# Commands

~course \<Course Initials\> \<Course Number\> \<Section Number\> \<Semester Name\> 

Examples:

~course CEN 4020 1 2022 Spring

~course CDA 3100 8 2021 Fall

# Notes
This project is actively under development, and this README will be cleaned up signifgantly when I feel it is actually in a spot that is usable.

**I do not reccomend using this program in its current state. I am not responsible for any ill effects that may come as a result of this program**

Big thanks to [@au5ton](https://github.com/au5ton) for his documentation of the [collegescheduler API]https://github.com/au5ton/docs/wiki/CollegeScheduler-(*.collegescheduler.com))
