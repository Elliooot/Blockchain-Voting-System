import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

const ProtectRoute: React.FC = () => {
    const {isAuthenticated} = useAuth();

    if(!isAuthenticated){
        return <Navigate to={"/login"} replace />
    }

    return <Outlet />
}

export default ProtectRoute;