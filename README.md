# HumanTaskDistributor
Simple command-line tool that helps automate the process of distributing sets of tasks to many people, including weighted preference and historical variation.

## Help
```
usage: HumanTaskDistributor
 -hl,--humans-list <arg>                Path to a CSV file containing list
                                        of humans to distribute tasks to.
                                        First column should be the name of
                                        the person, and second column can
                                        be empty, or contain a
                                        floating-point weight.
 -o,--output <arg>                      Output file to write CSV
                                        distribution data to.
 -prev,--previous-distributions <arg>   One or more CSV files containing
                                        previous task distribution
                                        results, to aid in balancing
                                        distribution over multiple
                                        iterations. Each should be of the
                                        form: person name, task name
 -tl,--tasks-list <arg>                 Path to a CSV file containing list
                                        of tasks that can be distributed
                                        to humans. First column should be
                                        unique task name.
```
