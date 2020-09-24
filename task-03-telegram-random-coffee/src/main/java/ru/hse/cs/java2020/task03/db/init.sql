USE tracker;

create table if not exists users (
    id int(11) NOT NULL,
    token varchar(50),
    company varchar(50),
    state varchar(50),
    auth boolean DEFAULT false,
    PRIMARY KEY (id)
);

create table if not exists tasks (
    id int(11) NOT NULL,
    queue varchar(255),
    summary varchar(1024),
    description varchar(1024),
    task varchar(50),
    count int(11),
    page int(11),
    PRIMARY KEY (id)
);