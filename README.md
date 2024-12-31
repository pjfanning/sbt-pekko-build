# sbt-pekko-build

Copies 2 features from different Pekko repos that are useful to share around our other Pekko repos.

* PekkoDependency has code to get latest release and snapshot versions for core Pekko jars
* Mima builds on sbt-mima-plugin and provides support for looking up exclude files in `mima-filters` directories.
    - see https://github.com/apache/incubator-pekko/blob/main/CONTRIBUTING.md#binary-compatibility
 
v0.3.0 and above also contains an autoplugin that enables Scala compile inlining (Scala 2 only). This behaviour cannot be enabled on any Pekko 1.0.x module.

The default is:
```
ThisBuild / pekkoInlineEnabled := true
```
On 1.0.x branches and on main branch if we haven't forked a 1.0.x branch yet, we must set:
```
ThisBuild / pekkoInlineEnabled := false
```

When running local builds you can use `sbt -Dpekko.no.inline` to avoid the inlining. It is very expensive to inline (uses a lot more CPU and memory when it is enabled).


## PekkoDependency

This is usable as is in v0.2.x but in v0.3.0 and above, you need to add a file to your `project` dir like [PekkoCoreDependency.scala](https://github.com/apache/incubator-pekko-http/pull/418/files#diff-1f66132a50db37ce33500827316ccde362d7ac385333d98eca70659b7b8edd55)

* defaults to `1.0.0` because we want to maintain compatibility with all Pekko 1 releases
* `-Dpekko.build.pekko.version=latest-release` will find latest release regardless of version number (December 2023 - this evals to `1.0.2`)
* `-Dpekko.build.pekko.version=main` will find latest snapshot for `main` branch (December 2023 - this evals to `1.1.0-M0+...-SNAPSHOT`)
* `-Dpekko.build.pekko.version=1.0.x` will find latest snapshot for `1.0.x` branch
* `-Dpekko.build.pekko.version=1.1.x` will find latest snapshot for `1.1.x` branch
