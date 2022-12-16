// User
post : user/login : form-data username/password
post : user/register : json user (username, full_name, password, email, gender)
post : user/upload_image : access token, param image, param optional username 
put : user/update : access token, json user(full_name, password, gender)
put : user/update_password : access token, json user(password)
get : user/diary : access token
get : user/profile : param username

// Diary
post : diary/save : access token, json diary (title, content, image_url, display)
put : diary/update : access token, json diary (title, content, image_url, display)
delete : diary/delete : access token, param id
get : diary/author={username} 
get : id={diary_id} 

// Comment
post : comment/add : access token, json comment(content, image_url)
get : comment/id={id}
put : comment : json comment (content, image_url)

// Public
get : / 
get : image/fileName
get : image/profile/fileName