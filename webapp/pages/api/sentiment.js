import axios from "axios";
const baseURL = "http://localhost:8080/api/";

export const getTweet = () => {
	return fetch("http://localhost:8080/api/view").then((res) => res.json());
};

export const searchTweet = async (keyword) => {
	const param = encodeURIComponent(keyword);
	return await fetch(baseURL + "search?keyword=" + param, {
		method: "POST",
	}).then((res) => res.json());
};
