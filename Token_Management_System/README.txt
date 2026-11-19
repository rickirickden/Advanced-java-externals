=====================================================
 TOKEN MANAGEMENT SYSTEM
 Advanced Java mini project (JSP / Servlets / JDBC)
=====================================================

WHAT THIS IS
------------
A small queue management system, like the token displays you see at
banks and clinics. Customers log in and take a token for a service,
staff (admin) open counters and call the next token, and a display
board in the waiting area shows which number is being served.

Roles:
  - USER  : take a token, see queue position and estimated wait,
            cancel a waiting token, see past tokens
  - ADMIN : dashboard with today's stats, add/open/close counters,
            call next / served / no-show, manage services

Everyone logs in at the same place; the menu changes based on role.

REQUIREMENTS
------------
1. JDK 8 or above
2. Eclipse IDE for Enterprise Java and Web Developers
   (the normal "Eclipse IDE for Java Developers" will NOT show the
   server options, so install the Enterprise one)
3. Apache Tomcat 9.0  (download zip from tomcat.apache.org)
4. MySQL server (XAMPP works fine) running on localhost:3306

The MySQL Connector/J jar and the JSTL jars are ALREADY inside
src/main/webapp/WEB-INF/lib, so you do not need to download anything.

SETUP (5 steps)
---------------
1.  Unzip the project, then in Eclipse:
    File > Import > General > Existing Projects into Workspace,
    select the "Token Management System" folder and finish.
    The Tomcat runtime (Apache Tomcat v9.0) should already appear in
    the project. If Eclipse complains, add it under
    Window > Preferences > Server > Runtime Environments.

2.  Create the database: open MySQL / phpMyAdmin and run the
    database.sql file that is in the project root. It creates the
    database token_db with 4 tables and some sample rows.

3.  Check the database login: open
    src/main/java/com/tms/util/DBConnection.java
    and set USER and PASS to your MySQL username/password
    (XAMPP default is root with an empty password, which is what
    the file already has).

4.  Right click the project > Run As > Run on Server,
    pick your Tomcat 9 server and finish.

5.  Open http://localhost:8080/Token_Management_System/

DEFAULT LOGINS
--------------
    admin / admin123     (admin role)
    user  / user123      (normal user)

You can also create your own account from the Register page.

HOW TO USE IT (quick demo flow)
-------------------------------
1.  Open the Display Board link in a second browser window
    (this is the waiting-area TV).
2.  Log in as "user" and take a token for a service.
    The home page shows your token number, your position in the
    queue and the estimated wait.
3.  Log out, log in as "admin", go to Counters, open the counter
    that handles that service and press "Call Next".
    Your number appears on the display board.
4.  Back as "user", the home page now says "please go to Counter X".
5.  Admin presses "Served" (or "No Show") to finish the token.

PROJECT STRUCTURE
-----------------
src/main/java/com/tms
    model/    User, Service, Counter, Token   (plain data classes)
    dao/      UserDao, ServiceDao, CounterDao, TokenDao (JDBC code)
    util/     DBConnection, PasswordUtil (SHA-256 hashing)
    filter/   AuthFilter (login + role check for /user and /admin)
    web/      one servlet per page/action

src/main/webapp
    index, login, register, board.jsp + error page
    user/  home, new token, history
    admin/ dashboard, counters, services
    includes/  shared header/footer/messages
    style.css  handwritten stylesheet (no framework, works offline)
    WEB-INF/lib  mysql-connector-j + jstl jars

database.sql     table creation + sample data (run once)

A FEW THINGS WORTH KNOWING FOR VIVA
-----------------------------------
- Token numbers restart every day and are per service
  (A-001, B-001...). TokenDao.issueToken is synchronized so two
  users clicking at the same moment can never get the same number.
- "Call Next" picks the token that has been waiting the longest for
  that counter's service. The UPDATE only succeeds if the token is
  still WAITING, so two counters cannot grab the same token.
- Passwords are stored as SHA-256 hashes, not plain text.
- All database queries use PreparedStatement, so values cannot be
  used for SQL injection.
- The display board is a plain JSP that refreshes itself with a
  meta refresh tag every 10 seconds, so it runs on any TV/browser.

KNOWN LIMITS (on purpose, it is a mini project)
-----------------------------------------------
- No HTTPS, no email/SMS alerts
- Admin accounts must be inserted in the database directly
- Estimated wait = people ahead x average minutes, it is a rough
  guess, not a real prediction
