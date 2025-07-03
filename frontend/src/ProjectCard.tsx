import './ProjectCard.css';

interface ProjectCardProps {
    imageUrl: string;
    title: string;
    description: string;
    link: string;
}

function ProjectCard(props: ProjectCardProps){
    return(
        <div className="project-card">
            <img src={props.imageUrl} alt={props.title} />
            <h3>{props.title}</h3>
            <p>{props.description}</p>
            <a href={props.link} target='_blank' rel='noopener noreferrer'>View Project</a>
        </div>
    );
}

export default ProjectCard;