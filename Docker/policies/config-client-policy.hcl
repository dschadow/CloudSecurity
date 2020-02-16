path "secret/*" {
  capabilities = ["read"]
}

path "secret/config-client-vault/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}