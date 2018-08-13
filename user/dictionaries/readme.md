## Instructions to work with dictionaries
### About dictionaries internal structure
Dictionaries are the files that contain information about each distinct word and about all words of the dictionary and about dictionary itself as a whole. 
So each dictionary consists of two sections a header and a content.
Information about the dictionary as a whole contains in the dictionary's header and information about words contains in the lines of content-section of the dictionary (one word per line).
There are two kinds of dictionaries - main dictionaries and user dictionaries.
There is no matter what kind of dictionary it is, they all consists of two parts - a header and a content
#### Header structure
The header of the dictionary starts from the beginning of the file and ends on the last line before the line with the word `start`.
The header contains lines with `name=value` pairs.
<br> In the current version there is two types of values:
- lang = [an ISO 639-1 name of language] e.g. lang=en or lang=ru etc. - indicates the language of words of the dictionary.
This value should be presented in every dictionary type - main and user's
- user = [some user identifier] - maybe the name of the user or his/here social number or something like this<br>
This value should be presented only in the users dictionaries.
  Here are some examples:
  - user=Ivan Smith <br>
  or 
  - user=014-629-247-39 etc.
#### Content structure
The content section of the dictionary starts immediately after the line with the word `start`. 
The content is a list of lines, let's call they records. 
Each record consists of three fields: 
- first is the ordinal number of the word, 
- the second it is the frequency indicator, this value has different meaning in main dictionaries and in users dictionaries:
  - for main dictionaries it is the IPM value (instances per million words in national corpus of language)
  - for users dictionaries it is the counter of word uses
- and the third field contains the word or phrase itself. 

Fields of the record are comma-separated and each line ends with CRLF symbol.

### About relations between different kinds of dictionaries and merging them
An instance of the Predictor class use simultaneously three types of dictionaries. File names of that three dictionaries are described in the `predictor.conf` file.
During initialization of the instance of the Predictor class, these three dictionaries are merged into one search dictionary.
Records of this search dictionary by default are ordered first by the user's frequency (we call it local frequency) and then by the frequency in IPM from the main dictionary (we call it frequency or global frequency)
There are some requirement and features for the dictionaries that we have specified in the predictor.conf file that we need to know:
- language (parameter `lang`) of each dictionary should be the same, if it differs at least in one dictionary the constructor of Predictor class will drop an error
- if a word exists in the main dictionary and in the user's dictionary then merging could be produced with or without updating
  - if they are merging with an update then IPM in global frequency field of search dictionary will be replaced with the user frequency (counter of uses) and stored as local frequency too.
  - if dictionaries merge without update then frequency field in the search dictionary will be equal to an IPM value from the main dictionary and users frequency will be saved in the field of local frequency
- if a word doesn't exist in the main dictionary then search dictionary will save word's uses counter as global frequency and as local frequency

