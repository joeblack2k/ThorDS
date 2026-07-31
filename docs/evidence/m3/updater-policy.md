# M3 updater policy

The `gitHubProd` product variant binds `NoUpdatesRepository` and
`NoUpdateInstallManager`. The merged debug APK has no
`android.permission.REQUEST_INSTALL_PACKAGES` permission. Its update content
provider is removed by `app/src/gitHubProd/AndroidManifest.xml`.

The GitHub installer remains reachable only from the `gitHubNightly` source
set. `app/src/gitHubProd/res/xml/pref_general_updates.xml` hides the upstream
check/channel settings from the ThorDS production UI. M3 has no product update
channel.

Validated by:

```text
CARGO=/tmp/thords-cargo ./gradlew --no-daemon \
  :app:assembleGitHubProdDebug :app:testGitHubProdReleaseUnitTest
result: PASS

release merged manifest:
- REQUEST_INSTALL_PACKAGES: absent
- UpdateContentProvider: absent
```
