CREATE TABLE TEAM_CHAMPIONSHIP(ID INTEGER PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(50) NOT NULL, NUM_OF_PLAYERS INT NOT NULL);
CREATE TABLE TEAM_VENUE(ID VARCHAR(20) PRIMARY KEY , NAME VARCHAR(50) NOT NULL, ADDRESS VARCHAR(200) NOT NULL, GPS VARCHAR(100));
CREATE TABLE TEAM_MEMBER(ID VARCHAR(30) PRIMARY KEY, NAME VARCHAR(100) NOT NULL, EMAIL VARCHAR(100) NOT NULL, ROLE VARCHAR(20) NOT NULL, PASSWORD_HASH VARCHAR(50));
CREATE TABLE TEAM_MATCH_DATA(ID INTEGER PRIMARY KEY AUTO_INCREMENT, CHAMP_ID INT NOT NULL, TIME TIMESTAMP NOT NULL, VENUE_ID VARCHAR(20) NOT NULL, OPPONENT VARCHAR(100) NOT NULL, GOALS_SCORED INT, GOALS_CONCEDED INT);
CREATE TABLE TEAM_MEMBER_STATEMENT(MATCH_ID INT, MEMBER_ID VARCHAR(30), TIME TIMESTAMP NOT NULL, MARK VARCHAR(50) NOT NULL, COMMENT TEXT, PRIMARY KEY(MATCH_ID, MEMBER_ID));
CREATE TABLE TEAM_GUEST_FOR_MATCH(MATCH_ID INT, GUEST_NAME VARCHAR(30), TIME TIMESTAMP NOT NULL, PRIMARY KEY(MATCH_ID, GUEST_NAME));
CREATE TABLE TEAM_GOALS(MATCH_ID INT, MEMBER_ID VARCHAR(30), SCORED INT, PRIMARY KEY(MATCH_ID, MEMBER_ID));
