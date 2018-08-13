## Setting up a configuration file
The initialization information for creating an instance of the Predictor class is in the `config/predictor.conf` file

In the same folder, there are template files with different configuration options.
To make one of them active, copy the contents of the file and put it into the file predictor.conf instead its previous content

## Structure of the predictor.conf and meaning of its fields
The predictor.conf file consists of comments and `name=value` pair strings.
Each line that begins with # is the comment line and will be ignored by the parser.
In the current version of the Predictor library there are three types of values
- dictionaries files names 
  - mainDictionary - name of the main dictionary file (e.g. mainDictionary = dictionaries/en-main-v1-utf8.dic)
  - userWordsDictionary - name of the user's words dictionary (e.g. userWordsDictionary = user/dictionaries/en-user-words-v1-utf8.dic)
  - userPhrasesDictionary - name of the user's phrases dictionary (e.g. userPhrasesDictionary = user/dictionaries/en-user-phrases-v1-utf8.dic)
- parameters of the NGram index
  - n = 2
- search parameters
  - maxDistance - maximal allowed editors distance between search pattern and the word or prefix of the word from search dictionary (e.g. maxDistance = 1)
  - prefix - prefix mode of searching flag (e.g. prefix=true)
