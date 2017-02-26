# Sync
case. '+ADD' button click으로 new Task를 추가하려는 경우  
- Server에 request를 하고 response가 올 때까지 Listview에 new task를 추가하지 않는다. 부자연?스러움.
- Server의 response를 기다리지 않고 입력한 new task를 add하여 view를 update한다. 만약 response가 400 code라면, add된 걸 다시 remove?
=> 한 Task에 대한 변화가 있을 때마다 Network 통신. editText에 대한 변화(Task 추가, 편집)가 있을 때마다 HTTP request, response 통신을 한다는 거 and UI update.

