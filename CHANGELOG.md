## [1.6.2](https://github.com/sphericalkat/atomic-demo/compare/v1.6.1...v1.6.2) (2023-05-25)


### Bug Fixes

* dial down the logging ([8d6a899](https://github.com/sphericalkat/atomic-demo/commit/8d6a8998969c96589f8f93afdb557eee887e3e3d))
* **transit:** pull off correct sender ([1958255](https://github.com/sphericalkat/atomic-demo/commit/195825541baf09de4f11af1fb8d7efb99d3eabd2))

## [1.6.1](https://github.com/sphericalkat/atomic-demo/compare/v1.6.0...v1.6.1) (2023-05-07)


### Bug Fixes

* most bugs related to discovery ([1fd9e42](https://github.com/sphericalkat/atomic-demo/commit/1fd9e42eb78e2a037cd2eb3d8e970972edf1f0db))

# [1.6.0](https://github.com/sphericalkat/atomic-demo/compare/v1.5.0...v1.6.0) (2023-05-07)


### Bug Fixes

* **transit:** prevent errors due to unimplemented handlers ([9079167](https://github.com/sphericalkat/atomic-demo/commit/90791677f42e523aa98d1764b7416297dd54aefe))


### Features

* initial attempt at discovery ([56cde63](https://github.com/sphericalkat/atomic-demo/commit/56cde63af0f6d7009db60b0d7be8c4741ddb2334))

# [1.5.0](https://github.com/sphericalkat/atomic-demo/compare/v1.4.0...v1.5.0) (2023-05-07)


### Features

* add methods for shutdown and deregistering ([2a6205f](https://github.com/sphericalkat/atomic-demo/commit/2a6205f841a82d3bc02c80efc50ac91c3e1d5f76))
* implement heartbeat ([76ad718](https://github.com/sphericalkat/atomic-demo/commit/76ad718fdfcde25e3c886687bd2e59fd490257e7))

# [1.4.0](https://github.com/sphericalkat/atomic-demo/compare/v1.3.0...v1.4.0) (2023-05-07)


### Bug Fixes

* **localbus:** check for existing event flows when collecting ([fc7e4ec](https://github.com/sphericalkat/atomic-demo/commit/fc7e4ec9f009eb146cd6c2cffa25bd2ead29cd9e))


### Features

* implement local event emitter ([de06400](https://github.com/sphericalkat/atomic-demo/commit/de06400689f29a4ab0769ec623ebded88db1627a))

# [1.3.0](https://github.com/sphericalkat/atomic-demo/compare/v1.2.0...v1.3.0) (2023-05-07)


### Features

* implement local node catalog ([f46e8e3](https://github.com/sphericalkat/atomic-demo/commit/f46e8e3424c39a97393d5851d1f26fc68fefdfc5))

# [1.2.0](https://github.com/sphericalkat/atomic-demo/compare/v1.1.0...v1.2.0) (2023-05-06)


### Features

* implement queue subscription ([4d2c7b1](https://github.com/sphericalkat/atomic-demo/commit/4d2c7b1a79a28a6bba73058278412b832dcfd580))
* implement transit class ([bc03d1f](https://github.com/sphericalkat/atomic-demo/commit/bc03d1f693f25ba2f97622856d34c5b6fd6e5206))
* **transport:** implement send ([e2f9c8e](https://github.com/sphericalkat/atomic-demo/commit/e2f9c8ead80214ff5b48a7e979313a7eaf4b8d0d))

# [1.1.0](https://github.com/sphericalkat/atomic-demo/compare/v1.0.1...v1.1.0) (2023-05-01)


### Features

* add packet types and handlers ([a696674](https://github.com/sphericalkat/atomic-demo/commit/a69667483242b205cd2ce8b21125503340e46868))
* begin work on request handler ([f13f65a](https://github.com/sphericalkat/atomic-demo/commit/f13f65a7c35bb2778cd2e3e98b248ca6aae02db0))
* implement serializers ([3857de5](https://github.com/sphericalkat/atomic-demo/commit/3857de57b488acd13774c2dc72b4463de23b3cbb))
* set up transporter structure ([9c599c2](https://github.com/sphericalkat/atomic-demo/commit/9c599c2623d754f4ba0d90ccee2da5a20b77e460))

## [1.0.1](https://github.com/sphericalkat/atomic-demo/compare/v1.0.0...v1.0.1) (2023-05-01)


### Bug Fixes

* **ci:** remove explicit release ([b1f5db7](https://github.com/sphericalkat/atomic-demo/commit/b1f5db7dc2f98cc43529c92f989f90245c03b2a6))

# 1.0.0 (2023-05-01)


### Bug Fixes

* **ci:** grant write permissions to github token ([b096c20](https://github.com/sphericalkat/atomic-demo/commit/b096c202a27e0f04d7f0715d79dce4a225da4888))
* **ci:** include github token and actor env vars ([3e7804e](https://github.com/sphericalkat/atomic-demo/commit/3e7804eef7cd42a9ca20163100cd5216f81a6812))
* **gradle:** add publication ([d53a898](https://github.com/sphericalkat/atomic-demo/commit/d53a8987c3a5e6ea35fe016df0738a980f0b5306))


### Features

* attempt setting up semantic release ([deebddc](https://github.com/sphericalkat/atomic-demo/commit/deebddc6f65c63460c766b9fa2867488d1e168e0))
* **ci:** add ability to publish to github maven repository ([84f67b3](https://github.com/sphericalkat/atomic-demo/commit/84f67b311e93e7262f9a62a34b96895c9aa18ff2))
* include version in publish ([2891a1a](https://github.com/sphericalkat/atomic-demo/commit/2891a1ace556d0142d5f413b8840c111ec75f166))
* tie atomic version to global project version ([81ace13](https://github.com/sphericalkat/atomic-demo/commit/81ace13dec31cdf1f659eb53a432d64c8e240af3))
