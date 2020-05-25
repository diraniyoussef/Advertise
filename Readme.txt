This app is a sort of advertisement to someone's business or profile or whatever. It's an easy way to make an app for anyone. Our client gets his admin version and then we distribute a client version on the Play store. A database is in the middle. The admin changes whatever he likes on his side and his clients (with the client version of the app) gets the info reflected in their apps.

In the admin app there is an internal SQLite database. Same in the admin's client app. 
The middle will also be an intermedite server with its own SQLite database.

Ideally, admin writes to his database and (then) updates the middle database and (then) the intermediate server will update the admin's client database.

The client's database is for the case where the client didn't opened the app but didn't have internet connection.