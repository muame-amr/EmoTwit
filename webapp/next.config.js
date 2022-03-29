/** @type {import('next').NextConfig} */
const nextConfig = {
	reactStrictMode: true,
	// async rewrites() {
	// 	return [
	// 		{
	// 			source: "/sentiment",
	// 			destination: "http://localhost:8080/", // Proxy to Backend
	// 		},
	// 	];
	// },
};

module.exports = nextConfig;
