# shop-telegram-bot
Shop in the telegram with the admin panel

## Technology stack
Java 8, Maven, Spring Boot, Spring MVC, Spring Data, Spring Security, Postgresql, Freemarker, HTML, Telegram Bots, Hibernate

## Quick start guide
1. Create postgres database and change the configuration in the properties `telegram-bot/src/main/resources/hibernate.cfg.xml` and `admin-panel/src/main/resources/application.properties`
2. Import the database schema `resources/db_schema.sql` and database data `resources/db_data.sql`
3. Set up the telegram bot username and token in the properties for telegram bot `telegram-bot/src/main/resources/application.properties`
4. Run the telegram bot and admin panel, web UI is accessible on `http://localhost:8080`

## Screenshots
#### Chatbot
![](images/1.jpg)
![](images/2.jpg)
![](images/3.jpg)
![](images/4.jpg)
![](images/5.jpg)
#### Admin panel
![](images/6.jpg)
![](images/7.jpg)
![](images/8.jpg)
![](images/9.jpg)
![](images/10.jpg)
