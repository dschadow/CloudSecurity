path "secret/*" {
  policy = "write"
}

path "auth/token/lookup-self" {
  policy = "read"
}