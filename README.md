# sbt-pekko-build

Copies 2 features from different Pekko repos that are useful to share around our other Pekko repos.

* PekkoDependency has code to get latest release and snapshot versions for core Pekko jars
* Mima builds on sbt-mima-plugin and provides support for looking up exclude files in `mima-filters` directories.
    - you'll find examples in https://github.com/apache/incubator-pekko

## PekkoDependency
* defaults to `1.0.0` because we want to maintain compatibility with all Pekko 1 releases
* `-Dpekko.build.pekko.version=latest-release` will find latest release regardless of version number (December 2023 - this evals to `1.0.2`)
* `-Dpekko.build.pekko.version=main` will find latest snapshot for `main` branch (December 2023 - this evals to `1.1.0-M0+...-SNAPSHOT`)
* `-Dpekko.build.pekko.version=1.0.x` will find latest snapshot for `1.0x` branch

