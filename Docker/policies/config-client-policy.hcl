path "kv-v2/*" {
  capabilities = ["read"]
}

path "kv-v2/my-secrets/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "secret/*" {
  capabilities = ["read"]
}

path "transit/*" {
  capabilities = ["read", "update"]
}

path "database/creds/config-client-vault-write" {
  capabilities = ["read"]
}