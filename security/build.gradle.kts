configurations.maybeCreate("default")
artifacts.add("default",file("security-pos-1.0.2.aar"))

configurations.maybeCreate("debug")
artifacts.add("debug",file("security-pos-1.0.2.aar"))

configurations.maybeCreate("release")
artifacts.add("release",file("security-pos-1.0.2.aar"))