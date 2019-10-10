# loading the libraries
import pandas as pd

qa_file = "qa-iot.csv"
qa_comments_file = "qa-comments-iot.csv"

# loading and checking the contents
qa_df = pd.read_csv(qa_file)
# print(qa_df)

qa_comment_df = pd.read_csv(qa_comments_file)
# print(qa_comment_df)

# now lets combine the contents
# print(qa_df.head())

for index, row in qa_df.head().iterrows():
    print(index, row['Id'])
