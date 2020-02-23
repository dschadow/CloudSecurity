path "kv-v2/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
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