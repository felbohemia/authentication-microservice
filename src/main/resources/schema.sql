
--DROP TABLE IF EXISTS challenge_attempt;
--IF NOT EXISTS

CREATE TABLE IF NOT EXISTS user
(
  user_Id int not null primary key AUTO_INCREMENT,
  alias varchar(255) not null unique,
  password varchar(255) not null

);