CREATE TABLE users(
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    password TEXT,
    roles LIST<TEXT>,
    emailactivationToken TEXT,
    emailactivatedat TIMESTAMP,
    secondname TEXT,
    secondrank TEXT,
    createdat TIMESTAMP,
    updatedat TIMESTAMP
);