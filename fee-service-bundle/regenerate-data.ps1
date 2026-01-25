# Script to regenerate account data with correct debit/credit logic
Write-Host "Building project..."
mvn clean package -DskipTests

Write-Host "`nRunning data regeneration..."
java -cp "target/fee-service-bundle.jar" com.example.university.fee.RegenerateAccountData

Write-Host "`nDone! You can now view the account statement with correct debit/credit logic."
