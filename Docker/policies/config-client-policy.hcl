path "secret/data/Config-Client-Vault" {
  capabilities = [ "read" ]
}

path "secret/data/Config-Client-Vault/*" {
  capabilities = [ "read" ]
}

path "secret/data/application" {
  capabilities = [ "read" ]
}

path "secret/data/application/*" {
  capabilities = [ "read" ]
}

path "secret/data/custom-secrets" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "transit/*" {
  capabilities = ["read", "update"]
}

path "database/creds/config_client_vault_all_privileges" {
  capabilities = ["read"]
}