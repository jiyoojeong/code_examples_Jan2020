CREATE TABLE parents AS
  SELECT "abraham" AS parent, "barack" AS child UNION
  SELECT "abraham"          , "clinton"         UNION
  SELECT "delano"           , "herbert"         UNION
  SELECT "fillmore"         , "abraham"         UNION
  SELECT "fillmore"         , "delano"          UNION
  SELECT "fillmore"         , "grover"          UNION
  SELECT "eisenhower"       , "fillmore";

CREATE TABLE dogs AS
  SELECT "abraham" AS name, "long" AS fur, 26 AS height UNION
  SELECT "barack"         , "short"      , 52           UNION
  SELECT "clinton"        , "long"       , 47           UNION
  SELECT "delano"         , "long"       , 46           UNION
  SELECT "eisenhower"     , "short"      , 35           UNION
  SELECT "fillmore"       , "curly"      , 32           UNION
  SELECT "grover"         , "short"      , 28           UNION
  SELECT "herbert"        , "curly"      , 31;

CREATE TABLE sizes AS
  SELECT "toy" AS size, 24 AS min, 28 AS max UNION
  SELECT "mini"       , 28       , 35        UNION
  SELECT "medium"     , 35       , 45        UNION
  SELECT "standard"   , 45       , 60;

-------------------------------------------------------------
-- PLEASE DO NOT CHANGE ANY SQL STATEMENTS ABOVE THIS LINE --
-------------------------------------------------------------

-- The size of each dog
CREATE TABLE size_of_dogs AS
  SELECT d.name AS name, s.size as size
    FROM dogs AS d, sizes AS s
    WHERE s.min < d.height AND d.height <= s.max;

-- All dogs with parents ordered by decreasing height of their parent
CREATE TABLE by_parent_height AS
  SELECT p.child AS name
    FROM dogs AS d, parents AS p
    WHERE d.name = p.parent
    ORDER BY -1*d.height;

-- Filling out this helper table is optional
CREATE TABLE siblings AS
  SELECT a.child AS first, b.child AS second
  FROM parents AS a, parents AS b
  WHERE first < second AND a.parent = b.parent;

-- Sentences about siblings that are the same size
CREATE TABLE sentences AS
  SELECT dogsibs.first || ' and ' || dogsibs.second || ' are ' || doggo1.size || ' siblings'
  FROM siblings as dogsibs, size_of_dogs as doggo1, size_of_dogs as doggo2
  WHERE doggo1.size = doggo2.size AND doggo1.name = dogsibs.first AND doggo2.name = dogsibs.second;

-- Ways to stack 4 dogs to a height of at least 170, ordered by total height
CREATE TABLE stacks_helper(dogs, stack_height, last_height);

-- Add your INSERT INTOs here
CREATE TABLE doggos (d1, d2, d3, d4);

-- first
INSERT INTO stacks_helper (dogs, stack_height, last_height)
   SELECT d.name, d.height, d.height
   FROM dogs as d;

-- second
INSERT INTO stacks_helper (dogs, stack_height, last_height)
   SELECT s.dogs || ', ' || d2.name, d2.height + stack_height, d2.height
   FROM dogs as d, dogs as d2, stacks_helper as s
   WHERE d2.name != s.dogs AND d2.height > s.last_height;

--SELECT * FROM stacks_helper;
-- third
INSERT INTO stacks_helper (dogs, stack_height, last_height)
   SELECT s.dogs || ', ' || d3.name, d3.height + stack_height, d3.height
   FROM dogs as d3, stacks_helper as s
   WHERE d3.name not in (s.dogs) AND d3.height > s.last_height;

--SELECT * FROM stacks_helper;
--fourth
INSERT INTO stacks_helper (dogs, stack_height, last_height)
   SELECT s.dogs || ', ' || d4.name, d4.height + stack_height, d4.height
   FROM dogs as d4, stacks_helper as s
   WHERE d4.name not in (s.dogs) AND d4.height > s.last_height;

--SELECT * FROM stacks_helper;

CREATE TABLE stacks AS
  SELECT DISTINCT s.dogs as dogs, s.stack_height as height
  FROM stacks_helper as s
  WHERE height > 170
  ORDER BY height ASC;
