CREATE TABLE reminder (
                          id BIGSERIAL PRIMARY KEY,
                          title VARCHAR(255),
                          description VARCHAR(4096),
                          remind TIMESTAMP,
                          user_id BIGINT NOT NULL,
                          CONSTRAINT fk_reminder_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users (id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE
);