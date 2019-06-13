# Command line options

## Run JavaSee

The following block lists example commands

```
# Generate java_see.yml
java -jar JavaSee-all.jar init

# Analyze all Java codes under current directory
java -jar JavaSee-all.jar check -config java_see.yml

# Analyze all Java codes under src
java -jar JavaSee-all.jar check -config java_see.yml -root src

# Analyze all Java coces under src and print the result with JSON
java -jar JavaSee-all.jar check -config java_see.yml -root src -format json

# Analyze all Java codes under current directory and print lines matching with println()
java -jar JavaSee-all.jar find "println(_)" .
```
