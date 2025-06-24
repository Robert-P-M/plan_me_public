import os
from fnmatch import fnmatch


def generate_tree_structure(start_path, ignore_patterns=None, allowed_subdirs=None):
    if ignore_patterns is None:
        ignore_patterns = []
    if allowed_subdirs is None:
        allowed_subdirs = []

    def should_ignore(name):
        return any(fnmatch(name, pattern) for pattern in ignore_patterns)

    def tree(dir_path, prefix=""):
        entries = sorted(os.listdir(dir_path))
        entries = [e for e in entries if not should_ignore(e)]
        tree_str = ""
        for i, entry in enumerate(entries):
            path = os.path.join(dir_path, entry)
            connector = "├── " if i < len(entries) - 1 else "└── "

            # Wenn es ein Verzeichnis ist:
            if os.path.isdir(path):
                # Falls allowed_subdirs gesetzt ist und wir im 'src' sind,
                # dann nur erlaubte Unterordner durchlaufen
                if os.path.basename(dir_path) == "src":
                    if entry not in allowed_subdirs:
                        continue

                tree_str += f"{prefix}{connector}{entry}/\n"
                extension = "│   " if i < len(entries) - 1 else "    "
                tree_str += tree(path, prefix + extension)
            else:
                tree_str += f"{prefix}{connector}{entry}*\n"
        return tree_str

    return tree(start_path)


if __name__ == "__main__":
    ignore = ["build", ".gradle", ".idea", ".git", "*.iml"]
    allowed = ["main"]  # nur diesen Ordner unter 'src' erlauben
    structure_text = generate_tree_structure(".", ignore_patterns=ignore, allowed_subdirs=allowed)

    with open("folder-structure.txt", "w", encoding="utf-8") as f:
        f.write("./\n" + structure_text)

    print("Ordnerstruktur wurde in folder-structure.txt geschrieben.")