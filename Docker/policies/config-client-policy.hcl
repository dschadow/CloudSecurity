path "secret/data/config-client-vault" {
  capabilities = [ "read" ]
}

path "secret/data/application" {
  capabilities = [ "read" ]
}

path "secret/data/custom-secrets" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "transit/*" {
  capabilities = ["read", "update"]
}

path "database/creds/config-client-vault-write" {
  capabilities = ["read"]
}