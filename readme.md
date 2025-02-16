##  查看所有分支
```agsl
git branch
```
## 查看所有标签
```agsl
git tag 
```

## 查看所有文件 --废弃
````

 git ls-tree -r --name-only dev | while read filename; do

    commit_info=$(git log -1 --format="%h %ad %s" --date=format:'%Y-%m-%dT%H:%M:%S' -- "$filename")
    
    echo "$filename $(echo $commit_info | awk '{print $1}') $(echo $commit_info | awk '{print $2, $3}') $(echo $commit_info | cut -d ' ' -f 4-)"
 done

````
## 查看文件信息 显示最近一次提交 提交hash 作者 时间 记录
````

git log -1 --pretty=format:"%h|%an|%ad|%s" --date=format:'%Y-%m-%d %H:%M:%S' --all --find-object=a51028c803dd1d280d62d45be122e61ba0335da6

````
## 查看所有提交记录
```agsl
 git log  --pretty=format:"%h|%an|%ad|%s" --date=format:'%Y-%m-%d %H:%M:%S' --all

```


## 查看一个tree下的文件 100644 blob 90012116c03db04344ab10d50348553aa94f1ea0    123.txt
```agsl 
 git  ls-tree 248543eef7f514adc2919d47a08cec497f04d3c7 
```
## 查看文件内容
```agsl
 git cat-file -p c9ae1a982371cfd60082a5ae831b4febc01bee73
```
## 根据提交hash显示被修该的文件
```agsl
git diff-tree --no-commit-id --name-only -r 248543e

```
## 查找父提交
```agsl
git show --pretty=format:"%P" -s 248543e
```
## 比较提交差异
```agsl
git diff fatherHash currentHash
```



su - umgk
git clone https://github.com/sitaramc/gitolite
mkdir $HOME/bin
gitolite/install -to $HOME/bin
ssh-keygen -t rsa
mv .ssh/id_rsa.pub admin.pub
$HOME/bin/gitolite setup -pk admin.pub
git clone git@127.0.0.1:gitolite-admin

