resolvers in ThisBuild += "lightbend-commercial-mvn" at
  "https://repo.lightbend.com/pass/ZIa00KKtEDsWcjWOwf_dRsBTiZGYXSWDKdN_ezkhsPDwvJ1T/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy",
  url("https://repo.lightbend.com/pass/ZIa00KKtEDsWcjWOwf_dRsBTiZGYXSWDKdN_ezkhsPDwvJ1T/commercial-releases"))(Resolver.ivyStylePatterns)