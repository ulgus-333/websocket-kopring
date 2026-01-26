let websocket = null;
let userIdx = null;
let userNickname = null;
let currentRoomIdx = null;
let selectedUser = null;

$(document).ready(function() {
    // Fetch user info first, then fetch chat rooms
    $.get("/user", function(data) {
        userIdx = data.idx;
        userNickname = data.nickName || data.name; // Use nickname, or name as fallback
        $("#userEmail").text(data.email);
        $("#userNickname").text(userNickname);
        fetchChatRoomsAndDisplay();
    }).fail(function() {
        alert("User not authenticated. Please log in.");
        // window.location.href = "/login"; // Optional: redirect to login
    });

    // Attach event listeners
    $("#send").on("click", sendMessage);
    $("#message").on("keypress", function(e) {
        if (e.which === 13) { // Enter key pressed
            sendMessage();
            return false; // Prevent default action (e.g., form submission)
        }
    });
    $("#hideChatBtn").on("click", backToInitialView);
    $("#leaveRoomBtn").on("click", backToInitialView);
    $("#backToInitialView").on("click", backToInitialView);
    $("#createRoomBtn").on("click", createRoom);
    $("#logoutBtn").on("click", () => {
        $("#logoutForm").submit();
    });
    $("#myPageBtn").on("click", () => { window.location.href = "/mypage.html"; });

    $("#attachFileBtn").on("click", () => {
        $("#fileInput").click();
    });

    $("#fileInput").on("change", handleFileSelect);


    // Modal-related event listeners
    $("#user-search-btn").on("click", searchUsers);
    $("#back-to-search-btn").on("click", backToSearchStep);
    $("#create-room-confirm-btn").on("click", createRoomConfirm);

    $('#user-search-results').on('click', '.list-group-item', function() {
        selectedUser = {
            idx: $(this).data('user-idx'),
            name: $(this).data('user-name')
        };
        $("#user-search-step").hide();
        $("#room-title-step").show();
    });


    // Use event delegation for dynamically added room items
    $("#chatRooms").on("click", ".list-group-item", function(e) {
        e.preventDefault();
        const roomIdx = $(this).data("room-idx");
        const roomName = $(this).data("room-name");
        connectToRoom(roomIdx, roomName);
    });
});

async function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file || !currentRoomIdx) {
        return;
    }

    // 1. Get Presigned URL from our backend
    const parRequest = {
        pathType: 'CHAT_ATTACHMENT',
        filename: file.name,
        variables: [currentRoomIdx, userIdx] // Pass room index as a variable
    };

    let parResponse;
    try {
        const response = await fetch('/files/presigned', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(parRequest)
        });
        if (!response.ok) {
            throw new Error('Failed to get pre-signed URL.');
        }
        parResponse = await response.json();
    } catch (error) {
        console.error(error);
        alert('Error preparing file for upload.');
        return;
    }

    // 2. Upload the file to OCI using the PAR
    try {
        const uploadResponse = await fetch(parResponse.parUrl, {
            method: 'PUT',
            headers: { 'Content-Type': file.type },
            body: file
        });
        if (!uploadResponse.ok) {
            throw new Error('File upload failed.');
        }
    } catch (error) {
        console.error(error);
        alert('Error uploading file.');
        return;
    }

    // 3. Send WebSocket message
    const messageType = file.type.startsWith('image/') ? 'IMAGE' : 'FILE';
    const chatMessage = {
        type: messageType,
        message: parResponse.filePath, // Send the file path, not the content
        roomIdx: currentRoomIdx,
        userIdx: userIdx,
        senderNickname: userNickname,
        createAt: getLocalDateTimeString()
    };

    if (websocket) {
        websocket.send(JSON.stringify(chatMessage));
    }

    // Reset file input to allow selecting the same file again
    $(event.target).val('');
}

function createRoom() {
    // Show the modal and reset it
    $('#createRoomModal').modal('show');
    backToSearchStep();
    $('#user-search-input-name').val('');
    $('#user-search-input-email').val('');
    $('#user-search-results').empty();
    $('#room-title-input').val('');
}

function backToSearchStep() {
    $("#room-title-step").hide();
    $("#user-search-step").show();
    selectedUser = null;
}

function searchUsers() {
    const name = $("#user-search-input-name").val();
    const email = $("#user-search-input-email").val();

    if (!name && !email) {
        return;
    }

    const searchData = {
        size: 10
    };

    if (name) {
        searchData.name = name;
    }
    if (email) {
        searchData.email = email;
    }

    $.get("/user/search", searchData)
        .done(function(response) {
            const resultsContainer = $("#user-search-results");
            resultsContainer.empty();
            if (response && response.users && response.users.content && response.users.content.length > 0) {
                const resultList = $("<div>").addClass("list-group");
                response.users.content.forEach(function(user) {
                    // Don't show the current user in the search results
                    if (user.idx === userIdx) {
                        return;
                    }
                    const userItem = $("<a>")
                        .addClass("list-group-item list-group-item-action")
                        .attr("href", "#")
                        .data("user-idx", user.idx)
                        .data("user-name", user.nickName || user.name)
                        .text((user.nickName || user.name) + ' (' + user.email + ')');
                    resultList.append(userItem);
                });
                resultsContainer.append(resultList);
            } else {
                resultsContainer.text("No users found.");
            }
        })
        .fail(function() {
            $("#user-search-results").text("Error searching for users.");
        });
}

function createRoomConfirm() {
    if (!selectedUser) {
        alert("Please select a user.");
        return;
    }

    const roomTitle = $("#room-title-input").val();

    const requestData = {
        title: roomTitle,
        invitedUserIdx: selectedUser.idx
    };

    $.ajax({
        url: "/chats/room",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(requestData),
        success: function(response) {
            $('#createRoomModal').modal('hide');
            fetchChatRoomsAndDisplay();
            // Optionally, connect to the new room immediately
            // connectToRoom(response.idx, response.title);
        },
        error: function() {
            alert("Error creating chat room.");
        }
    });
}

function fetchChatRoomsAndDisplay() {
    $.get("/chats/rooms", function(response) {
        $("#chatRooms").empty();
        if (response && response.rooms && response.rooms.content && response.rooms.content.length > 0) {
            const roomListContainer = $("<div>").addClass("list-group");
            response.rooms.content.forEach(function(room) {
                addRoomToList(room, roomListContainer);
            });
            $("#chatRooms").append(roomListContainer);
        } else {
            $("#chatRooms").append("<p>No chat rooms found.</p>");
        }
    }).fail(function() {
        $("#chatRooms").append("<p>Error loading chat rooms.</p>");
    });
}

function addRoomToList(room, roomListContainer) {
    const roomTitle = room.title || room.users.join(', ');
    const roomName = room.title || room.users.join(', ');
    const roomItem = $("<a>")
        .addClass("list-group-item list-group-item-action d-flex justify-content-between align-items-center")
        .attr("href", "#")
        .data("room-idx", room.idx)
        .data("room-name", roomName)
        .data("unread-count", room.unreadMessageCount); // Store unread count

    const titleAndParticipantsDiv = $("<div>");
    const titleH5 = $("<h5>").addClass("mb-1").text(roomTitle);
    if (room.unreadMessageCount > 0) {
        const badge = $("<span>")
            .addClass("badge badge-danger badge-pill ml-2")
            .attr("id", `badge-for-room-${room.idx}`) // Add unique ID
            .text(room.unreadMessageCount);
        titleH5.append(badge);
    }
    titleAndParticipantsDiv.append(titleH5);
    titleAndParticipantsDiv.append($("<small>").text(room.users.length + " participants"));

    const timeSmall = $("<small>");
    if (room.lastMessagedAt) {
        timeSmall.text(new Date(room.lastMessagedAt).toLocaleTimeString());
    }

    roomItem.append(titleAndParticipantsDiv);
    roomItem.append(timeSmall);


    if (roomListContainer) {
        roomListContainer.append(roomItem);
    } else {
        let container = $("#chatRooms .list-group");
        if (container.length === 0) {
            container = $("<div>").addClass("list-group");
            $("#chatRooms").append(container);
        }
        container.append(roomItem);
    }
}


function connectToRoom(roomIdx, roomName) {
    if (websocket) {
        websocket.close();
    }
    $("#messages").empty();

    const url = 'ws://localhost:8081/ws/chats/' + roomIdx;
    console.log(url)
    websocket = new WebSocket(url);

    websocket.onopen = function(event) {
        console.log('Connected to WebSocket: ', event);
        currentRoomIdx = roomIdx;
        $("#chatRoomName").text(roomName);

        // Fetch previous messages
        fetchPreviousMessages(currentRoomIdx);

        // Reset unread message count
        resetUnreadCount(currentRoomIdx);

        $("#roomListColumn").removeClass("col-md-12").addClass("col-md-3");
        $("#chatAreaColumn").show();
        $("#backToInitialView").show();
    };

    websocket.onmessage = function(event) {
        console.log('Received message: ', event.data);
        const message = JSON.parse(event.data);
        showMessage(message);
    };

    websocket.onerror = function(error) {
        console.error('WebSocket Error: ', error);
        alert('WebSocket connection error. See console for details.');
    };

    websocket.onclose = function(event) {
        console.log('WebSocket Disconnected: ', event);
        // Additional cleanup if needed
    };
}

function resetUnreadCount(roomIdx) {
    $.ajax({
        url: "/chats/" + roomIdx,
        type: "PATCH",
        success: function() {
            // Update the data attribute for consistency
            const roomItem = $(`#chatRooms .list-group-item[data-room-idx="${roomIdx}"]`);
            if (roomItem.length) {
                roomItem.data("unread-count", 0);
            }
            
            // Remove the badge using its unique ID
            $(`#badge-for-room-${roomIdx}`).remove();
        },
        error: function(xhr, status, error) {
            console.error("Failed to reset unread count:", error);
        }
    });
}

function fetchPreviousMessages(roomIdx) {
    const messagesContainer = $("#messages");
    messagesContainer.empty(); // Clear previous messages

    $.get(`/chats/${roomIdx}`)
        .done(function(response) {
            if (response && response.messages && response.messages.content) {
                // Messages are DESC, reverse to show oldest first
                const messages = response.messages.content.reverse();
                messages.forEach(function(message) {
                    showMessage(message);
                });
            }
        })
        .fail(function() {
            messagesContainer.append("<p>Could not fetch previous messages.</p>");
        });
}

function disconnect() {
    if (websocket !== null) {
        websocket.close();
    }
    websocket = null;
    currentRoomIdx = null;
    console.log("Disconnected");
}

function backToInitialView() {
    disconnect();
    $("#chatAreaColumn").hide();
    $("#roomListColumn").removeClass("col-md-3").addClass("col-md-12");
    $("#backToInitialView").hide();
}

function sendMessage() {
    const messageContent = $("#message").val();
    if (messageContent && websocket && currentRoomIdx) {
        const chatMessage = {
            type: 'TEXT', // Assuming 'TEXT' is the type expected by the backend
            message: messageContent,
            roomIdx: currentRoomIdx, // Include roomIdx for backend routing/context
            userIdx: userIdx, // Include senderIdx if the backend needs it
            senderNickname: userNickname, // Include senderNickname if the backend needs it
            createAt: getLocalDateTimeString()
        };
        websocket.send(JSON.stringify(chatMessage));
        $("#message").val("");
    }
}

function showMessage(message) {
    const messagesContainer = $("#messages");
    const isMyMessage = message.user.idx === userIdx;

    const alignment = isMyMessage ? 'right' : 'left';

    const messageContainer = $("<div>").addClass(`message-container ${alignment}`);
    const bubble = $("<div>").addClass(`message-bubble ${alignment}`);
    const sender = $("<div>").addClass('sender-name');
    
    let content;
    if (message.type === 'IMAGE') {
        content = $('<a>').attr('href', message.message).attr('target', '_blank');
        content.append($('<img>').attr('src', message.message).addClass('img-thumbnail').css({'max-width': '300px', 'max-height': '300px'}));
    } else if (message.type === 'FILE') {
        const decodedFileName = decodeURIComponent(message.message.split('/').pop());
        content = $('<a>').attr('href', message.message).attr('target', '_blank').addClass('file-card');
        const fileInfo = $('<div>').addClass('file-info');
        fileInfo.append($('<div>').addClass('file-name').text(decodedFileName));
        // You can add file size here if available in the message
        // fileInfo.append($('<div>').addClass('file-size').text('1.2 MB'));
        content.append(fileInfo);
    } else {
        content = $("<div>").text(message.message || '');
    }

    if (!isMyMessage) {
        let senderNickname = message.user.nickName ? message.user.nickName : message.user.name
        sender.text(senderNickname || 'Unknown');
        bubble.append(sender);
    }

    bubble.append(content); // Use the new content element
    messageContainer.append(bubble);
    messagesContainer.append(messageContainer);

    // Scroll to the bottom
    messagesContainer.scrollTop(messagesContainer[0].scrollHeight);
}

function getLocalDateTimeString() {
    const now = new Date();

    const pad = (n) => String(n).padStart(2, "0");

    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}` +
        `T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
}