#!/bin/bash

# Usage: ./release.sh 1.0.1

VERSION=$1

if [[ -z "$VERSION" ]]; then
  echo "❌ Please provide a version number (e.g. 1.0.1)"
  exit 1
fi

echo "🔧 Updating version to $VERSION in all pom.xml files..."

# find . -name "pom.xml"
# - Searches recursively from the current
# - Finds all files named pom.xml

# -exec sed ... {} +
#   - For each pom.xml file found, execute the sed command.
#   - The {} is replaced with the filename, and + means run sed on as many files as possible at once (performance optimization).

# sed -i '' "s/<version>.*<\/version>/<version>$VERSION<\/version>/g"
# This is the actual substitution.
# 1. -i ''
#   - Means “edit the file in place” (no backup).
#   - The '' is required on macOS (it’s an empty string for a backup file name).
# 	- On Linux, you’d usually just write -i.
#
# 2. "s/.../.../g" → This is the sed substitution syntax:
#   - s = substitute
#   - pattern = what to search for
#   - replacement = what to replace it with
#   - g = global — replace all matches on the line
find . -name "pom.xml" -exec sed -i '' "s/<version>.*<\/version>/<version>$VERSION<\/version>/g" {} +

# Now that we have done some great work of updating the POM files
# Let's now commit the pom file changes and then TAG the milestone.
echo "📦 Committing version bump..."
git add .
git commit -m "🔖 Release version $VERSION"
git tag "v$VERSION"

# Finally we can PUSH both the Branch changes and the tag we have just created.
echo "🚀 Pushing commit and tag..."
git push origin master
git push origin "v$VERSION"

echo "✅ Release $VERSION complete!"