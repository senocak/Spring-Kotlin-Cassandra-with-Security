# Spring Kotlin Cassandra

## Setup

```cassandraql
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
```

```cassandraql
//CREATE INDEX ON asenocak.users(name);
CREATE CUSTOM INDEX name_idx ON asenocak.users (name)
    USING 'org.apache.cassandra.index.sasi.SASIIndex'
    WITH OPTIONS = {'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};
```