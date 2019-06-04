# Command line options

## Run JavaSee

The following block lists example commands

```
# Generate java_see.yml
java -jar JavaSee-all.jar init

# Analyze all source Java codes under current directory
java -jar JavaSee-all.jar -config java_see.yml

# Analyze all source Java codes under src
java -jar JavaSee-all.jar -config java_see.yml -root src

# Analyze all source Java coces under src and print the result with JSON
java -jar JavaSee-all.jar -config java_see.yml -root src -format json
```
